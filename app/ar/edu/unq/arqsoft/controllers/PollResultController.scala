package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.mappings.json.PlayJsonDTOFormats
import ar.edu.unq.arqsoft.services.PollResultService
import com.google.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{ControllerComponents, PlayBodyParsers}

import scala.util.Try

@Singleton
class PollResultController @Inject()(cc: ControllerComponents, parse: PlayBodyParsers,
                                     pollResultService: PollResultService
                                    )
  extends BasicController(cc, parse) with PlayJsonDTOFormats {

  def get(studentFileNumber: Int, careerShortName: String, pollKey: String) = Action {
    Ok(Json.toJson(Try(pollResultService.getOrNew(studentFileNumber, careerShortName, pollKey)).get))
  }

}
