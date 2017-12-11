package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api.InputAlias.PollDeltaDTO
import ar.edu.unq.arqsoft.api.PollResultDTO
import ar.edu.unq.arqsoft.model._
import com.google.inject.Singleton
import org.joda.time.DateTime

@Singleton
class PollResultService extends Service {

  def pollResultFor(studentFileNumber: Int, careerShortName: String, pollKey: String): PollResultDTO = inTransaction {
    val resultOption =
      PollResultDAO.resultsOfStudentForPoll(
        StudentDAO.whereFileNumber(studentFileNumber),
        PollDAO.pollsOfCareerWithKey(CareerDAO.whereShortName(careerShortName), pollKey)
      )
        .singleOption
    val result = resultOption.getOrElse(updatedPollResult(studentFileNumber, careerShortName, pollKey))
    result
  }

  protected def newPollResult(studentFileNumber: Int, careerShortName: String, pollKey: String): PollResult = inTransaction {
    val pollQuery = PollDAO.pollsOfCareerWithKey(CareerDAO.whereShortName(careerShortName), pollKey)
    val student = StudentDAO.whereFileNumber(studentFileNumber).single
    val poll = pollQuery.single
    val pollSubjects = SubjectDAO.subjectsOfPoll(pollQuery).toList
    val defaultOption = OfferDAO.baseOfferOfNonCourse(NonCourseDAO.notYetOption).single
    val newResult = PollResult(student.id, poll.id, DateTime.now)
    PollResultDAO.save(newResult)
    val selectedOptions = pollSubjects.map(subject => PollSelectedOption(newResult.id, subject.id, defaultOption.id))
    PollSelectedOptionDAO.save(selectedOptions)
    newResult
  }

  protected def updatedPollResult(studentFileNumber: Int, careerShortName: String, pollKey: String): PollResult = inTransaction {
    val baseResult = newPollResult(studentFileNumber, careerShortName, pollKey)
    val alreadyPassedOption = OfferDAO.baseOfferOfNonCourse(NonCourseDAO.alreadyPassedOption).single
    val passedSubjects = SubjectDAO.subjectsWithOption(
      PollSelectedOptionDAO.optionsOfPoll(PollResultDAO.resultsOfStudentForPoll(StudentDAO.whereFileNumber(studentFileNumber), PollDAO.pollsOfCareer(CareerDAO.whereShortName(careerShortName)))),
      OfferDAO.baseOfferOfNonCourse(NonCourseDAO.alreadyPassedOption),
      SubjectDAO.subjectsOfPoll(PollDAO.pollsOfCareerWithKey(CareerDAO.whereShortName(careerShortName), pollKey))
    )
    val optionsToUpdate = PollSelectedOptionDAO.optionsOfPollWithSubject(PollResultDAO.find(baseResult.id), passedSubjects)
    PollSelectedOptionDAO.updateSelectionTo(optionsToUpdate, alreadyPassedOption.id)
    baseResult
  }

  def update(studentFileNumber: Int, careerShortName: String, pollKey: String, delta: PollDeltaDTO): PollResultDTO = inTransaction {
    if (delta.nonEmpty) {
      // Ensure it exists
      pollResultFor(studentFileNumber, careerShortName, pollKey)

      val possibleOptions = this.possibleOptions(careerShortName, pollKey, delta.keys)

      val pollResultQuery = PollResultDAO.resultsOfStudentForPoll(
        StudentDAO.whereFileNumber(studentFileNumber),
        PollDAO.pollsOfCareerWithKey(CareerDAO.whereShortName(careerShortName), pollKey)
      )
      val usedSubjectsQuery = SubjectDAO.subjectsOfCareerWithName(CareerDAO.whereShortName(careerShortName), delta.keys)
      val affectedOptions = PollSelectedOptionDAO.addOptionsOfPollWithSubject(pollResultQuery, usedSubjectsQuery).toList

      applyDelta(delta, affectedOptions, possibleOptions)
    }
    pollResultFor(studentFileNumber, careerShortName, pollKey)
  }

  protected def possibleOptions(careerShortName: String, pollKey: String, subjectShortNames: Iterable[String]): Iterable[(Subject, OfferOption, OfferOptionBase)] = inTransaction {
    val careerQuery = CareerDAO.whereShortName(careerShortName)
    val pollOfferOptionsQuery = PollOfferOptionDAO.optionsOfPoll(PollDAO.pollsOfCareerWithKey(careerQuery, pollKey))
    val usedSubjectsQuery = SubjectDAO.subjectsOfCareerWithName(careerQuery, subjectShortNames)
    val usedOptionsQuery = OfferDAO.baseOfferOfPollOfferWithSubject(pollOfferOptionsQuery, usedSubjectsQuery)
    // Squeryl lacks support for UNION queries so...
    val courseOptions = CourseDAO.addCoursesOfSubjectOffer(usedOptionsQuery).toList
    val nonCoursesOptions = NonCourseDAO.addNonCoursesOfSubjectOffer(usedOptionsQuery).toList

    courseOptions ++ nonCoursesOptions
  }

  protected def applyDelta(delta: PollDeltaDTO,
                 afectedOptions: Iterable[(Subject, PollSelectedOption)],
                 possibleOptions: Iterable[(Subject, OfferOption, OfferOptionBase)]
                ): Unit = inTransaction {
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
  }

}
