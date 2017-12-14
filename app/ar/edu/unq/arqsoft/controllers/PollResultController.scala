package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.api.InputAlias.PollDeltaDTO
import ar.edu.unq.arqsoft.mappings.json.PlayJsonDTOFormats
import ar.edu.unq.arqsoft.services.PollResultService
import com.google.inject.{Inject, Singleton}
import play.api.mvc.{ControllerComponents, PlayBodyParsers, Request}

@Singleton
class PollResultController @Inject()(cc: ControllerComponents, parse: PlayBodyParsers,
                                     pollResultService: PollResultService
                                    )
  extends BasicController(cc, parse) with PlayJsonDTOFormats {

  def get(studentFileNumber: Int, careerShortName: String, pollKey: String) = JsonAction {
    pollResultService.pollResultFor(studentFileNumber, careerShortName, pollKey)
  }

  def patch(studentFileNumber: Int, careerShortName: String, pollKey: String) = JsonAction withBody[PollDeltaDTO] {
    implicit request: Request[PollDeltaDTO] =>
      pollResultService.update(studentFileNumber, careerShortName, pollKey, request.body)
  }

  def allOfStudent(studentFileNumber: Int) = JsonAction {
    pollResultService.pollResultsOf(studentFileNumber)
  }

}
