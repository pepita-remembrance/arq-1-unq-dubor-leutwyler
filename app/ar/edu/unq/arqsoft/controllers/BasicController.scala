package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.logging.Logging
import ar.edu.unq.arqsoft.mappings.json.PlayJsonDTOFormats
import ar.edu.unq.arqsoft.maybe._
import play.api.libs.json.{JsError, Json, Reads, Writes}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future //This import provides the implicit execution context for Play json.validate

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

  def BasicSecured[A](username: String, password: String)(action: Action[A]): Action[A] = Action.async(action.parser) { request =>
    request.headers.get("Authorization").flatMap { authorization =>
      authorization.split(" ").drop(1).headOption.filter { encoded =>
        new String(org.apache.commons.codec.binary.Base64.decodeBase64(encoded.getBytes)).split(":").toList match {
          case u :: p :: Nil if u == username && password == p => true
          case _ => false
        }
      }
    }.map(_ => action(request)).getOrElse {
      Future.successful(Unauthorized.withHeaders("WWW-Authenticate" -> """Basic realm="Secured Area""""))
    }
  }

}

trait MaybeToJsonResult extends Results with Logging {
  def convert[A: Writes](maybe: Maybe[A]): Result = maybe match {
    case Just(()) => NoContent
    case Just(obj) => Ok(Json.toJson(obj))
    case BadLogin => Unauthorized(BadLogin.message)
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