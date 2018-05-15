package ar.edu.unq.arqsoft.security

import ar.edu.unq.arqsoft.logging.Logging
import play.api.mvc.{Request, Result, Results}

trait ActionByRole[Self <: ActionByRole[Self]] extends Logging {
  protected def requiredRoles: Seq[Role]

  protected def jwtService: JWTService

  def requires(role: Role, roles: Role*): Self

  def ifAuthorizedDo[T](request: Request[T])(code: => Result): Result =
    if (isAuthorized(request)) code
    else Results.Forbidden(unathorizedMessage(request))

  protected def unathorizedMessage(request: Request[_]): String =
    s"You are not authorized to use route: $request"

  protected def isAuthorized(request: Request[_]): Boolean = {
    (request.cookies.get("x-inscription-poll-token").map(_.value), requiredRoles) match {
      case (_, Nil) => true
      case (Some(encodedJWT), roles) if jwtService.isValidToken(encodedJWT) => {
        for {
          decodedJWT <- jwtService.decodePayload(encodedJWT)
          userRoleKey <- decodedJWT.get("role")
          roleFactory <- Role.fromJWTKey(userRoleKey)
          userRole = roleFactory(decodedJWT.get("fileNumber").map(_.toInt))
        } yield roles.exists(_ matches userRole)
      }.getOrElse(false)
      case _ => false
    }
  }
}
