package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api.{LoginDTO, UserDTO}
import ar.edu.unq.arqsoft.maybe.{BadLogin, Maybe}
import ar.edu.unq.arqsoft.security.JWTService
import com.google.inject.{Inject, Singleton}

@Singleton
class LoginService @Inject()(jwtService: JWTService,
                             adminService: AdminService,
                             studentService: StudentService) {

  def login(loginDTO: LoginDTO): Maybe[(UserDTO, String)] =
    adminService.login(loginDTO)
      .recover(studentService.login(loginDTO))
      .recover(BadLogin)
      .map { case (user, claims) => (user, jwtService.createToken(claims)) }
}
