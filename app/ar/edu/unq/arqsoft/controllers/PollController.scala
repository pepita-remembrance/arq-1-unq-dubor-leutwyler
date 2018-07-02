package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.api.CreatePollDTO
import ar.edu.unq.arqsoft.mappings.json.PlayJsonDTOFormats
import ar.edu.unq.arqsoft.security.{JWTService, RoleAdmin, RoleStudent}
import ar.edu.unq.arqsoft.services.PollService
import com.google.inject.Inject
import play.api.mvc.{ControllerComponents, PlayBodyParsers, Request}

class PollController @Inject()(cc: ControllerComponents, parse: PlayBodyParsers, jwtService: JWTService,
                               pollService: PollService
                              )
  extends BasicController(cc, parse, jwtService) with PlayJsonDTOFormats {

  def create(careerShortName: String) = JsonAction.withBody[CreatePollDTO].requires(RoleAdmin) {
    implicit request: Request[CreatePollDTO] =>
      pollService.create(careerShortName, request.body)
  }

  def allOfCareer(careerShortName: String) = JsonAction.requires(RoleAdmin) {
    pollService.allOf(careerShortName)
  }

  def get(careerShortName: String, pollKey: String) = JsonAction.requires(RoleStudent, RoleAdmin) {
    pollService.byCareerShortNameAndPollKey(careerShortName, pollKey)
  }

  def allOfStudent(studentFileNumber: Int) = JsonAction.requires(RoleStudent(studentFileNumber)) {
    pollService.allOf(studentFileNumber)
  }

}
