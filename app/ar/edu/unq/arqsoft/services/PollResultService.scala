package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api.InputAlias.PollDeltaDTO
import ar.edu.unq.arqsoft.api.{PollResultDTO, TallyDTO}
import ar.edu.unq.arqsoft.maybe.{Maybe, Something}
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

  def pollResultFor(studentFileNumber: Int, careerShortName: String, pollKey: String): Maybe[PollResultDTO] =
    getPollResult(studentFileNumber, careerShortName, pollKey)

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
      selectedOptions = pollSubjects.map(subject => PollSelectedOption(newResult.id, subject.id, defaultOption._2.id))
      _ <- pollSelectedOptionRepository.save(selectedOptions)
    } yield newResult

  protected def updatedPollResult(student: Student, poll: Poll): Maybe[PollResult] =
    for {
      baseResult <- newPollResult(student, poll)
      alreadyPassedOption <- nonCourseRepository.byKey(NonCourseOption.alreadyPassed)
      optionsToUpdate <- pollSelectedOptionRepository.getOfAlreadyPassedSubjects(baseResult)
      _ = optionsToUpdate.foreach(_.offerId = alreadyPassedOption._2.id)
      pollSelectedOptionRepository.update(optionsToUpdate)
    } yield baseResult

  // TODO: Continue from here!
  def update(studentFileNumber: Int, careerShortName: String, pollKey: String, delta: PollDeltaDTO, updateDate: DateTime = DateTime.now): Maybe[PollResultDTO] = {
    if (delta.nonEmpty) {
      // Ensure it exists
      val result = getPollResult(studentFileNumber, careerShortName, pollKey).get

      val possibleOptions = this.possibleOptions(careerShortName, pollKey, delta.keys)

      val affectedOptions = PollSelectedOptionDAO.optionsByPollResultAndSubjectsWithSubject(studentFileNumber, careerShortName, pollKey, delta.keys).toList

      applyDelta(delta, affectedOptions, possibleOptions.get)

      result.fillDate = updateDate
      PollResultDAO.update(result)
    }
    Something(getPollResult(studentFileNumber, careerShortName, pollKey).get)
  }

  protected def possibleOptions(careerShortName: String, pollKey: String, subjectShortNames: Iterable[String]): Maybe[Iterable[(Subject, OfferOption, OfferOptionBase)]] = {
    // Squeryl lacks support for UNION queries so...
    val courseOptions = CourseDAO.coursesForSubjectsOfPollWithBaseOffer(subjectShortNames, careerShortName, pollKey).toList
    val nonCoursesOptions = NonCourseDAO.nonCoursesForSubjectsOfPollWithBaseOffer(subjectShortNames, careerShortName, pollKey).toList

    Something(courseOptions ++ nonCoursesOptions)
  }

  protected def applyDelta(delta: PollDeltaDTO,
                           afectedOptions: Iterable[(Subject, PollSelectedOption)],
                           possibleOptions: Iterable[(Subject, OfferOption, OfferOptionBase)]
                          ): Maybe[Unit] = {
    val changed = afectedOptions.map { case (subject, pso) =>
      val selectedOption = delta(subject.shortName)
      val selectedOffer = possibleOptions.find { case (sub, offer, base) =>
        sub == subject &&
          offer.key == selectedOption.key &&
          base.isCourse == selectedOption.isCourse
      }.get._3
      pso.offerId = selectedOffer.id
      pso
    }
    PollSelectedOptionDAO.update(changed)
    Something(())
  }

}
