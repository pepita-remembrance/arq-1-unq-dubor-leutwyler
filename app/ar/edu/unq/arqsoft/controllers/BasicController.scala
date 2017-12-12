package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.logging.Logging
import ar.edu.unq.arqsoft.mappings.json.PlayJsonDTOFormats
import play.api.libs.json.{JsError, Json, Reads, Writes}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try} //This import provides the implicit execution context for Play json.validate

class BasicController(cc: ControllerComponents, parse: PlayBodyParsers)
  extends AbstractController(cc)
    with PlayJsonDTOFormats
    with Logging {

  def validateJson[A: Reads]: BodyParser[A] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  def JsonAction = new JsonActionBuilder

  class JsonActionWithBodyBuilder[In] {
    def apply[Out: Writes](block: Request[In] => Out)(implicit reads: Reads[In]): Action[In] = Action(validateJson[In]) {
      implicit request: Request[In] =>
        Try(block(request)) match {
          case Success(obj) => Ok(Json.toJson(obj))
          case Failure(ex) =>
            error("Error ocurred: ", ex)
            InternalServerError("BOOM!")
        }
    }
  }

  class JsonActionBuilder {
    def apply[Out: Writes](block: => Out): Action[AnyContent] = Action {
      Try(block) match {
        case Success(obj) => Ok(Json.toJson(obj))
        case Failure(ex) =>
          error("Error ocurred: ", ex)
          InternalServerError("BOOM!")
      }
    }

    def withBody[In]: JsonActionWithBodyBuilder[In] = new JsonActionWithBodyBuilder[In]
  }

}