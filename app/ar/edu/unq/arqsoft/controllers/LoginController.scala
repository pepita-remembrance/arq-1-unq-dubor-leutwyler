package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.api.LoginDTO
import ar.edu.unq.arqsoft.maybe.{BadLogin, Just}
import ar.edu.unq.arqsoft.services.LoginService
import com.google.inject.Inject
import play.api.mvc._

class LoginController @Inject()(cc: ControllerComponents, parse: PlayBodyParsers, loginService: LoginService)
  extends BasicController(cc, parse) {

  def login = JsonAction withBody[LoginDTO] {
    implicit request: Request[LoginDTO] =>
      loginService.login(request.body) match {
        case Just(token) => NoContent.withCookies(Cookie("x-inscription-poll-token", token)).bakeCookies()
        case _ => Unauthorized(BadLogin.message)
      }
  }

  def logout = Action {
    NoContent.discardingCookies(DiscardingCookie("x-inscription-poll-token"))
  }

}
