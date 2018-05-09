package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.logging.Logging
import ar.edu.unq.arqsoft.mappings.json.PlayJsonDTOFormats
import ar.edu.unq.arqsoft.maybe._
import ar.edu.unq.arqsoft.security.{ActionByRole, JWTService, Role}
import play.api.libs.json.{JsError, Json, Reads, Writes}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global //This import provides the implicit execution context for Play json.validate

class BasicController(cc: ControllerComponents, parse: PlayBodyParsers, jwtService: JWTService)
  extends AbstractController(cc) with Logging
    with PlayJsonDTOFormats
    with MaybeToJsonResult {
  controller =>

  def validateJson[A: Reads]: BodyParser[A] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  def JsonAction = new JsonActionBuilder(Seq.empty[Role])

  class JsonActionWithBodyBuilder[In](protected val requiredRoles: Seq[Role]) extends ActionByRole[JsonActionWithBodyBuilder[In]] {
    def apply[Out: Writes](block: Request[In] => Maybe[Out])(implicit reads: Reads[In]): Action[In] = Action(validateJson[In]) {
      request => ifAuthorizedDo(request)(convert(block(request)))
    }

    def apply(block: Request[In] => Result)(implicit reads: Reads[In]): Action[In] = Action(validateJson[In]) {
      request => ifAuthorizedDo(request)(block(request))
    }

    def requires(role: Role, roles: Role*): JsonActionWithBodyBuilder[In] =
      new JsonActionWithBodyBuilder[In](role +: roles)

    protected def jwtService: JWTService = controller.jwtService
  }

  class JsonActionBuilder(protected val requiredRoles: Seq[Role]) extends ActionByRole[JsonActionBuilder] {
    def apply[Out: Writes](block: => Maybe[Out]): Action[AnyContent] = Action {
      request => ifAuthorizedDo(request)(convert(block))
    }

    def apply(block: => Result): Action[AnyContent] = Action {
      request => ifAuthorizedDo(request)(block)
    }

    def withBody[In]: JsonActionWithBodyBuilder[In] =
      new JsonActionWithBodyBuilder[In](requiredRoles)

    def requires(role: Role, roles: Role*): JsonActionBuilder =
      new JsonActionBuilder(role +: roles)

    protected def jwtService: JWTService = controller.jwtService
  }

}

trait MaybeToJsonResult extends Results with Logging {
  protected def convert[A: Writes](maybe: Maybe[A]): Result = maybe match {
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