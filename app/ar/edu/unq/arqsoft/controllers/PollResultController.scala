package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.api.InputAlias.PollDeltaDTO
import ar.edu.unq.arqsoft.mappings.json.PlayJsonDTOFormats
import ar.edu.unq.arqsoft.security.{JWTService, RoleAdmin, RoleStudent}
import ar.edu.unq.arqsoft.services.PollResultService
import com.google.inject.Inject
import play.api.mvc.{ControllerComponents, PlayBodyParsers, Request}

class PollResultController @Inject()(cc: ControllerComponents, parse: PlayBodyParsers, jwtService: JWTService,
                                     pollResultService: PollResultService
                                    )
  extends BasicController(cc, parse, jwtService) with PlayJsonDTOFormats {

  def get(studentFileNumber: Int, careerShortName: String, pollKey: String) =
    JsonAction.requires(RoleStudent(studentFileNumber)) {
      pollResultService.pollResultFor(studentFileNumber, careerShortName, pollKey)
    }

  def create(studentFileNumber: Int, careerShortName: String, pollKey: String) =
    JsonAction.requires(RoleStudent(studentFileNumber)) {
      pollResultService.newPollResult(studentFileNumber, careerShortName, pollKey)
    }

  def patch(studentFileNumber: Int, careerShortName: String, pollKey: String) =
    JsonAction.withBody[PollDeltaDTO].requires(RoleStudent(studentFileNumber)) {
      implicit request: Request[PollDeltaDTO] =>
        pollResultService.update(studentFileNumber, careerShortName, pollKey, request.body)
    }

  def tally(careerShortName: String, pollKey: String) = JsonAction.requires(RoleAdmin) {
    pollResultService.tally(careerShortName: String, pollKey: String)
  }

}
