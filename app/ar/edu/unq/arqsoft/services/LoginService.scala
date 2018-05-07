package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api.LoginDTO
import ar.edu.unq.arqsoft.maybe.{BadLogin, Maybe}
import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import com.google.inject.{Inject, Singleton}

@Singleton
class LoginService @Inject()(adminService: AdminService,
                             studentService: StudentService) {

  def login(loginDTO: LoginDTO): Maybe[String] =
    adminService.login(loginDTO)
      .recover(studentService.login(loginDTO))
      .recover(BadLogin)
      .map(createToken)

  val JwtSecretKey = "secretKey"
  val JwtSecretAlgo = "HS256"

  def createToken(claimsSet: JwtClaimsSet): String = {
    val header = JwtHeader(JwtSecretAlgo)
    JsonWebToken(header, claimsSet, JwtSecretKey)
  }

  def isValidToken(jwtToken: String): Boolean =
    JsonWebToken.validate(jwtToken, JwtSecretKey)

  def decodePayload(jwtToken: String): Option[String] =
    jwtToken match {
      case JsonWebToken(header, claimsSet, signature) => Option(claimsSet.asJsonString)
      case _ => None
    }
}
