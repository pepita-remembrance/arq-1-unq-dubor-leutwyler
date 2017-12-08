package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api.PollResultDTO
import com.google.inject.Singleton

@Singleton
class PollResultService extends Service {

  def getOrNew(studentFileNumber: Int, careerShortName: String, pollKey: String): PollResultDTO = inTransaction {
    ???
  }

}
