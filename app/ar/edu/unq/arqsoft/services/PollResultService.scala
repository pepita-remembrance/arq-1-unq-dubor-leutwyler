package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api.PollResultDTO
import ar.edu.unq.arqsoft.model.PollResult
import com.google.inject.Singleton
import org.joda.time.DateTime

@Singleton
class PollResultService extends Service {

  def pollResultFor(studentFileNumber: Int, careerShortName: String, pollKey: String): PollResultDTO = inTransaction {
    val result =
      PollResultDAO.resultsOf(
        StudentDAO.whereFileNumber(studentFileNumber),
        PollDAO.pollsOf(CareerDAO.whereShortName(careerShortName), pollKey)
      )
        .singleOption
        .getOrElse(newPollResult(studentFileNumber, careerShortName, pollKey))
    result
  }

  protected def newPollResult(studentFileNumber: Int, careerShortName: String, pollKey: String): PollResult = {
    val student = StudentDAO.whereFileNumber(studentFileNumber).single
    val poll = PollDAO.pollsOf(CareerDAO.whereShortName(careerShortName), pollKey).single
    val notYetOption = NonCourseDAO.notYetOption.single
    val newResult = PollResult(student.id, poll.id, DateTime.now)
    PollResultDAO.save(newResult)

    newResult
  }

}
