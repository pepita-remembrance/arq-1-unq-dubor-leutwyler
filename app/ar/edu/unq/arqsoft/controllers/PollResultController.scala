package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.mappings.json.PlayJsonDTOFormats
import ar.edu.unq.arqsoft.services.PollResultService
import com.google.inject.{Inject, Singleton}
import play.api.mvc.{ControllerComponents, PlayBodyParsers}

@Singleton
class PollResultController @Inject()(cc: ControllerComponents, parse: PlayBodyParsers,
                                     pollResultService: PollResultService
                                    )
  extends BasicController(cc, parse) with PlayJsonDTOFormats {

  def get(studentFileNumber: Int, careerShortName: String, pollKey: String, default: Option[String]) = JsonAction {
    pollResultService.pollResultFor(studentFileNumber, careerShortName, pollKey, default)
  }

}
