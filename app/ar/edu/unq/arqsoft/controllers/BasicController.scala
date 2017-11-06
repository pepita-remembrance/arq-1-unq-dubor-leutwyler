package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.mappings.json.PlayJsonDTOFormats
import play.api.libs.json.{JsError, Reads}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global //This import provides the implicit execution context for Play json.validate

class BasicController(cc: ControllerComponents, parse: PlayBodyParsers) extends AbstractController(cc) with PlayJsonDTOFormats {

  def validateJson[A: Reads]: BodyParser[A] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

}