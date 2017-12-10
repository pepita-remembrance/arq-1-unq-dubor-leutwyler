package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api.PollResultDTO
import ar.edu.unq.arqsoft.model.{NonCourseOption, PollResult, PollSelectedOption}
import com.google.inject.Singleton
import org.joda.time.DateTime
import org.squeryl.Query

@Singleton
class PollResultService extends Service {

  def pollResultFor(studentFileNumber: Int, careerShortName: String, pollKey: String, defaultOptionText: Option[String]): PollResultDTO = inTransaction {
    val resultOption =
      PollResultDAO.resultsOf(
        StudentDAO.whereFileNumber(studentFileNumber),
        PollDAO.pollsOf(CareerDAO.whereShortName(careerShortName), pollKey)
      )
        .singleOption
    val defaultOptionQuery = defaultOptionText.map(NonCourseDAO.whereTextValue(_)).getOrElse(NonCourseDAO.notYetOption)
    val result = resultOption.getOrElse(updatedPollResult(studentFileNumber, careerShortName, pollKey, defaultOptionQuery))
    result
  }

  def newPollResult(studentFileNumber: Int, careerShortName: String, pollKey: String, defaultOptionQuery: Query[NonCourseOption]): PollResult = inTransaction {
    val pollQuery = PollDAO.pollsOf(CareerDAO.whereShortName(careerShortName), pollKey)
    val student = StudentDAO.whereFileNumber(studentFileNumber).single
    val poll = pollQuery.single
    val pollSubjects = SubjectDAO.subjectsOf(pollQuery).toList
    val defaultOption = OfferDAO.baseOfferOf(defaultOptionQuery).single._2
    val newResult = PollResult(student.id, poll.id, DateTime.now)
    PollResultDAO.save(newResult)
    val selectedOptions = pollSubjects.map(subject => PollSelectedOption(newResult.id, subject.id, defaultOption.id))
    PollSelectedOptionDAO.save(selectedOptions)
    newResult
  }

  def updatedPollResult(studentFileNumber: Int, careerShortName: String, pollKey: String, defaultOptionQuery: Query[NonCourseOption]): PollResult = inTransaction {
    val baseResult = newPollResult(studentFileNumber, careerShortName, pollKey, defaultOptionQuery)

    baseResult
  }

}
