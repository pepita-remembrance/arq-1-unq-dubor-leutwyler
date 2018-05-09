package ar.edu.unq.arqsoft.security

import ar.edu.unq.arqsoft.logging.Logging
import play.api.mvc.{Request, Result, Results}

trait ActionByRole[Self <: ActionByRole[Self]] extends Logging {
  protected def requiredRoles: Seq[Role]

  protected def jwtService: JWTService

  def requires(role: Role, roles: Role*): Self

  def ifAuthorizedDo[T](request: Request[T])(code: => Result): Result =
    if (isAuthorized(request)) code
    else Results.Unauthorized(unathorizedMessage(request))

  protected def unathorizedMessage(request: Request[_]): String =
    s"You are not authorized to use route: $request"

  protected def isAuthorized(request: Request[_]): Boolean = {
    // TODO: Do the thing!
    (request.cookies.get("x-inscription-poll-token").map(_.value), requiredRoles) match {
      case (_, Nil) => true
      case (Some(encodedJWT), roles) => info(s"Got jwt: $encodedJWT and roles: $roles"); true
      case _ => false
    }
  }
}
