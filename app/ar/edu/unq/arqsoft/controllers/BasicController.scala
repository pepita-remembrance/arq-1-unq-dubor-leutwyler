package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.logging.Logging
import ar.edu.unq.arqsoft.mappings.json.PlayJsonDTOFormats
import ar.edu.unq.arqsoft.maybe._
import play.api.libs.json.{JsError, Json, Reads, Writes}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global //This import provides the implicit execution context for Play json.validate

class BasicController(cc: ControllerComponents, parse: PlayBodyParsers)
  extends AbstractController(cc)
    with PlayJsonDTOFormats {

  def validateJson[A: Reads]: BodyParser[A] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  def JsonAction = new JsonActionBuilder

  class JsonActionWithBodyBuilder[In] extends MaybeToJsonResult {
    def apply[Out: Writes](block: Request[In] => Maybe[Out])(implicit reads: Reads[In]): Action[In] = Action(validateJson[In]) {
      request: Request[In] => convert(block(request))
    }
  }

  class JsonActionBuilder extends MaybeToJsonResult {
    def apply[Out: Writes](block: => Maybe[Out]): Action[AnyContent] = Action {
      convert(block)
    }

    def withBody[In]: JsonActionWithBodyBuilder[In] = new JsonActionWithBodyBuilder[In]
  }

}

trait MaybeToJsonResult extends Results with Logging {
  def convert[A: Writes](maybe: Maybe[A]): Result = maybe match {
    case Just(()) => NoContent
    case Just(obj) => Ok(Json.toJson(obj))
    case notFound: EntityNotFound =>
      NotFound(notFound.message)
    case manyNotFound: NotFounds =>
      BadRequest(manyNotFound.message)
    case saveError: SaveError =>
      BadRequest(saveError.message)
    case Nothing(msg) =>
      error(s"Error ocurred: $msg")
      InternalServerError("BOOM!")
  }
}