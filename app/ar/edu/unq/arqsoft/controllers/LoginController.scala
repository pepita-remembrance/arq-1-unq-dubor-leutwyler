package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.api.LoginDTO
import ar.edu.unq.arqsoft.services.LoginService
import com.google.inject.Inject
import play.api.mvc.{ControllerComponents, PlayBodyParsers, Request}

class LoginController @Inject()(cc: ControllerComponents, parse: PlayBodyParsers, loginService: LoginService)
  extends BasicController(cc, parse) {

  def login = JsonAction withBody[LoginDTO] {
    implicit request: Request[LoginDTO] =>
      loginService.login(request.body)
  }

}
