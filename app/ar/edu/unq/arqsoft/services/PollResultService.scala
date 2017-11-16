package ar.edu.unq.arqsoft.services

import javax.inject.Inject

import ar.edu.unq.arqsoft.DAOs._
import ar.edu.unq.arqsoft.api.PollResultDTO
import com.google.inject.Singleton

@Singleton
class PollResultService @Inject()(pollResultDAO: PollResultDAO) extends Service {

  def getOrNew(studentFileNumber: Int, careerShortName: String, pollKey: String): PollResultDTO = inTransaction {
    ???
  }

}
