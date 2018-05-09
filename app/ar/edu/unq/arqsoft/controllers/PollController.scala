package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.api.CreatePollDTO
import ar.edu.unq.arqsoft.mappings.json.PlayJsonDTOFormats
import ar.edu.unq.arqsoft.security.{JWTService, Role}
import ar.edu.unq.arqsoft.services.PollService
import com.google.inject.{Inject, Singleton}
import play.api.mvc.{ControllerComponents, PlayBodyParsers, Request}

@Singleton
class PollController @Inject()(cc: ControllerComponents, parse: PlayBodyParsers, jwtService: JWTService,
                               pollService: PollService
                              )
  extends BasicController(cc, parse, jwtService) with PlayJsonDTOFormats {

  def create(careerShortName: String) = JsonAction.withBody[CreatePollDTO].requires(Role.Admin) {
    implicit request: Request[CreatePollDTO] =>
      pollService.create(careerShortName, request.body)
  }

  def allOfCareer(careerShortName: String) = JsonAction.requires(Role.Admin) {
    pollService.allOf(careerShortName)
  }

  def get(careerShortName: String, pollKey: String) = JsonAction.requires(Role.AnyRole) {
    pollService.byCareerShortNameAndPollKey(careerShortName, pollKey)
  }

  def allOfStudent(studentFileNumber: Int) = JsonAction.requires(Role.Student) {
    pollService.allOf(studentFileNumber)
  }

}
