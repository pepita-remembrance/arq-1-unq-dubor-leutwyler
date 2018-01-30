package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api.InputAlias.PollDeltaDTO
import ar.edu.unq.arqsoft.api.{PollResultDTO, TallyDTO}
import ar.edu.unq.arqsoft.model._
import com.google.inject.Singleton
import org.joda.time.DateTime

@Singleton
class PollResultService extends Service {

  def tally(careerShortName: String, pollKey: String): Iterable[TallyDTO] = inTransaction {
    // Squeryl lacks support for UNION queries so...
    val courseTally = CourseDAO.tallyForPoll(careerShortName, pollKey).toList
    val nonCourseTally = NonCourseDAO.tallyForPoll(careerShortName, pollKey).toList

    (courseTally ++ nonCourseTally)
      .groupBy(_._1)
      .mapValues(_.groupBy(_._2: OfferOption))
      .mapValues(_.mapValues(_.map(_._3)))
      .mapAs[TallyDTO]
  }

  def pollResultFor(studentFileNumber: Int, careerShortName: String, pollKey: String): PollResultDTO = inTransaction {
    getPollResult(studentFileNumber, careerShortName, pollKey)
  }

  protected def getPollResult(studentFileNumber: Int, careerShortName: String, pollKey: String): PollResult = inTransaction {
    PollResultDAO.resultByStudentAndPoll(studentFileNumber, careerShortName, pollKey)
      .singleOption
      .getOrElse(updatedPollResult(studentFileNumber, careerShortName, pollKey))
  }

  protected def newPollResult(studentFileNumber: Int, careerShortName: String, pollKey: String): PollResult = inTransaction {
    val pollQuery = PollDAO.pollByCareerAndKey(careerShortName, pollKey)
    val student = StudentDAO.whereFileNumber(studentFileNumber).single
    val poll = pollQuery.single
    val pollSubjects = SubjectDAO.subjectsOfPoll(careerShortName, pollKey).toList
    val defaultOption = OfferDAO.baseOfferOfNonCourse(NonCourseOption.notYet).single
    val newResult = PollResult(poll.id, student.id, DateTime.now)
    PollResultDAO.save(newResult)
    val selectedOptions = pollSubjects.map(subject => PollSelectedOption(newResult.id, subject.id, defaultOption.id))
    PollSelectedOptionDAO.save(selectedOptions)
    newResult
  }

  protected def updatedPollResult(studentFileNumber: Int, careerShortName: String, pollKey: String): PollResult = inTransaction {
    val baseResult = newPollResult(studentFileNumber, careerShortName, pollKey)
    val alreadyPassedOption = OfferDAO.baseOfferOfNonCourse(NonCourseOption.alreadyPassed).single
    val optionsToUpdate = PollSelectedOptionDAO.optionsByPollResultAndPassedSubjectsOfStudent(baseResult.id, studentFileNumber)
    PollSelectedOptionDAO.updateSelectionTo(optionsToUpdate, alreadyPassedOption.id)
    baseResult
  }

  def update(studentFileNumber: Int, careerShortName: String, pollKey: String, delta: PollDeltaDTO, updateDate: DateTime = DateTime.now): PollResultDTO = inTransaction {
    if (delta.nonEmpty) {
      // Ensure it exists
      val result = getPollResult(studentFileNumber, careerShortName, pollKey)

      val possibleOptions = this.possibleOptions(careerShortName, pollKey, delta.keys)

      val affectedOptions = PollSelectedOptionDAO.optionsByPollResultAndSubjectsWithSubject(studentFileNumber, careerShortName, pollKey, delta.keys).toList

      applyDelta(delta, affectedOptions, possibleOptions)

      result.fillDate = updateDate
      PollResultDAO.update(result)
    }
    getPollResult(studentFileNumber, careerShortName, pollKey)
  }

  protected def possibleOptions(careerShortName: String, pollKey: String, subjectShortNames: Iterable[String]): Iterable[(Subject, OfferOption, OfferOptionBase)] = inTransaction {
    // Squeryl lacks support for UNION queries so...
    val courseOptions = CourseDAO.coursesForSubjectsOfPollWithBaseOffer(subjectShortNames, careerShortName, pollKey).toList
    val nonCoursesOptions = NonCourseDAO.nonCoursesForSubjectsOfPollWithBaseOffer(subjectShortNames, careerShortName, pollKey).toList

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
