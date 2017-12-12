package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.api.CreatePollDTO
import ar.edu.unq.arqsoft.mappings.json.PlayJsonDTOFormats
import ar.edu.unq.arqsoft.services.PollService
import com.google.inject.{Inject, Singleton}
import play.api.mvc.{ControllerComponents, PlayBodyParsers, Request}

@Singleton
class PollController @Inject()(cc: ControllerComponents, parse: PlayBodyParsers,
                               pollService: PollService
                              )
  extends BasicController(cc, parse) with PlayJsonDTOFormats {

  def create(careerShortName: String) = JsonActionWithBody[CreatePollDTO] {
    implicit request: Request[CreatePollDTO] =>
      pollService.create(careerShortName, request.body)
  }

  def allOfCareer(careerShortName: String) = JsonAction {
    pollService.allOf(careerShortName)
  }

  def get(careerShortName: String, pollKey: String) = JsonAction {
    pollService.byCareerShortNameAndPollKey(careerShortName, pollKey)
  }

  def allOfStudent(studentFileNumber:Int) = JsonAction {
    pollService.allOf(studentFileNumber)
  }

}
