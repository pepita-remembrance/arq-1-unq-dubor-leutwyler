package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api.PollResultDTO
import ar.edu.unq.arqsoft.model.PollResult
import com.google.inject.Singleton

@Singleton
class PollResultService extends Service {

  def getOrNew(studentFileNumber: Int, careerShortName: String, pollKey: String): PollResultDTO = inTransaction {
    PollResultDAO.resultsOf(
      StudentDAO.whereFileNumber(studentFileNumber),
      PollDAO.pollsOf(CareerDAO.whereShortName(careerShortName), pollKey)
    )
      .singleOption
      .getOrElse(newPollResult(studentFileNumber, careerShortName, pollKey))
  }

  protected def newPollResult(studentFileNumber: Int, careerShortName: String, pollKey: String): PollResult = {
    ???
  }

}
