package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api.InputAlias.PollDeltaDTO
import ar.edu.unq.arqsoft.api.{PollResultDTO, TallyDTO}
import ar.edu.unq.arqsoft.maybe.Maybe
import ar.edu.unq.arqsoft.model._
import ar.edu.unq.arqsoft.repository._
import com.google.inject.{Inject, Singleton}
import org.joda.time.DateTime

@Singleton
class PollResultService @Inject()(courseRepository: CourseRepository,
                                  nonCourseRepository: NonCourseRepository,
                                  pollRepository: PollRepository,
                                  careerRepository: CareerRepository,
                                  studentRepository: StudentRepository,
                                  pollResultRepository: PollResultRepository,
                                  subjectRepository: SubjectRepository,
                                  pollSelectedOptionRepository: PollSelectedOptionRepository
                                 ) extends Service {

  def tally(careerShortName: String, pollKey: String): Maybe[Iterable[TallyDTO]] =
    for {
      career <- careerRepository.byShortName(careerShortName)
      poll <- pollRepository.byKeyOfCareer(pollKey, career)
      courseTally <- courseRepository.tallyByPoll(poll)
      nonCourseTally <- nonCourseRepository.tallyByPoll(poll)
    } yield (courseTally ++ nonCourseTally)
      .groupBy(_._1)
      .mapValues(_.groupBy(_._2: OfferOption))
      .mapValues(_.mapValues(_.map(_._3)))
      .mapAs[TallyDTO]

  def pollResultFor(studentFileNumber: Int, careerShortName: String, pollKey: String): Maybe[PollResultDTO] =
    getPollResult(studentFileNumber, careerShortName, pollKey).as[PollResultDTO]

  protected def getPollResult(studentFileNumber: Int, careerShortName: String, pollKey: String): Maybe[PollResult] =
    for {
      career <- careerRepository.byShortName(careerShortName)
      poll <- pollRepository.byKeyOfCareer(pollKey, career)
      student <- studentRepository.byFileNumber(studentFileNumber)
      result <- pollResultRepository.byStudentAndPoll(student, poll, career)
        .recover(updatedPollResult(student, poll))
    } yield result

  protected def newPollResult(student: Student, poll: Poll): Maybe[PollResult] =
    for {
      pollSubjects <- subjectRepository.getOfPoll(poll)
      defaultOption <- nonCourseRepository.byKey(NonCourseOption.notYet)
      newResult = PollResult(poll.id, student.id, DateTime.now)
      _ <- pollResultRepository.save(newResult)
      selectedOptions = pollSubjects.map(subject => PollSelectedOption(newResult.id, subject.id, defaultOption.offerId))
      _ <- pollSelectedOptionRepository.save(selectedOptions)
    } yield newResult

  protected def updatedPollResult(student: Student, poll: Poll): Maybe[PollResult] =
    for {
      baseResult <- newPollResult(student, poll)
      alreadyPassedOption <- nonCourseRepository.byKey(NonCourseOption.alreadyPassed)
      optionsToUpdate <- pollSelectedOptionRepository.getOfAlreadyPassedSubjects(baseResult)
      _ = optionsToUpdate.foreach(_.offerId = alreadyPassedOption.offerId)
      _ <- pollSelectedOptionRepository.update(optionsToUpdate)
    } yield baseResult

  def update(studentFileNumber: Int, careerShortName: String, pollKey: String, delta: PollDeltaDTO, updateDate: DateTime = DateTime.now): Maybe[PollResultDTO] =
    for {
      result <- getPollResult(studentFileNumber, careerShortName, pollKey)
      availableOptions <- possibleOptions(result, delta.keys)
      affectedOptions <- pollSelectedOptionRepository.getOfPollResultBySubjectName(result, delta.keys)
      _ <- applyDelta(delta, affectedOptions, availableOptions)
      _ = result.fillDate = updateDate
      _ <- pollResultRepository.update(result)
    } yield result.as[PollResultDTO]

  protected def possibleOptions(pollResult: PollResult, subjectShortNames: Iterable[String]): Maybe[Iterable[(Subject, OfferOption)]] =
    for {
    // Squeryl lacks support for UNION queries so...
      courseOptions <- courseRepository.getOfPollResultBySubjectName(pollResult, subjectShortNames)
      nonCoursesOptions <- nonCourseRepository.getOfPollResultBySubjectName(pollResult, subjectShortNames)
    } yield courseOptions ++ nonCoursesOptions

  protected def applyDelta(delta: PollDeltaDTO,
                           afectedOptions: Iterable[(Subject, PollSelectedOption)],
                           availableOptions: Iterable[(Subject, OfferOption)]
                          ): Maybe[Unit] = {
    val changed = for {
      (subject, pso) <- afectedOptions
      selectedOption = delta(subject.shortName)
      (_, selectedOffer) = availableOptions.find { case (sub, offer) =>
        sub == subject &&
          offer.key == selectedOption.key &&
          offer.isCourse == selectedOption.isCourse
      }.get
      _ = pso.offerId = selectedOffer.offerId
    } yield pso
    pollSelectedOptionRepository.update(changed)
  }
}
