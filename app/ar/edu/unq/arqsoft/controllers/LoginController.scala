package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.api.{AdminDTO, LoginDTO, StudentDTO, UserDTO}
import ar.edu.unq.arqsoft.maybe.{BadLogin, Just}
import ar.edu.unq.arqsoft.security.{JWTService, Role}
import ar.edu.unq.arqsoft.services.LoginService
import com.google.inject.Inject
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc._

class LoginController @Inject()(cc: ControllerComponents, parse: PlayBodyParsers, loginService: LoginService, jwtService: JWTService)
  extends BasicController(cc, parse, jwtService) {

  implicit val userDTOWrites = new Writes[UserDTO] {
    def writes(o: UserDTO): JsValue = o match {
      case user: StudentDTO => studentDTOWrites.writes(user)
      case user: AdminDTO => adminDTOWrites.writes(user)
    }
  }

  def login = JsonAction.withBody[LoginDTO] {
    implicit request: Request[LoginDTO] =>
      loginService.login(request.body) match {
        case Just((user, token)) =>
          Ok(Json.toJson(user))
            .withCookies(Cookie("x-inscription-poll-token", token))
            .bakeCookies()
        case _ =>
          Unauthorized(BadLogin.message)
      }
  }

  def logout = JsonAction.requires(Role.AnyRole) {
    NoContent.discardingCookies(DiscardingCookie("x-inscription-poll-token"))
  }

}
