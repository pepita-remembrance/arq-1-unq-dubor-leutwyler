package ar.edu.unq.arqsoft.security

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}

class JWTService {

  val JwtSecretKey = "secretKey"
  val JwtSecretAlgo = "HS256"

  def createToken(claimsSet: JwtClaimsSet): String = {
    val header = JwtHeader(JwtSecretAlgo)
    JsonWebToken(header, claimsSet, JwtSecretKey)
  }

  def isValidToken(jwtToken: String): Boolean =
    JsonWebToken.validate(jwtToken, JwtSecretKey)

  def decodePayload(jwtToken: String): Option[Map[String, String]] =
    jwtToken match {
      case JsonWebToken(_, claimsSet, _) => claimsSet.asSimpleMap.toOption
      case _ => None
    }

}
