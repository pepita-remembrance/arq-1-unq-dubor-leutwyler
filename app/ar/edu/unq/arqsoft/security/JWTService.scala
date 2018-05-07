package ar.edu.unq.arqsoft.security

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import com.google.inject.Singleton

@Singleton
class JWTService {

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
