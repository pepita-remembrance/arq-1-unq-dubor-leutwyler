package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api.LoginDTO
import ar.edu.unq.arqsoft.maybe.Maybe
import ar.edu.unq.arqsoft.model.{TableRow, User}
import ar.edu.unq.arqsoft.repository.UserRepository
import ar.edu.unq.arqsoft.security.Role.Role
import ar.edu.unq.arqsoft.utils.Hash
import authentikat.jwt.JwtClaimsSet

abstract class UserService[T <: User with TableRow](repository: UserRepository[T], defaultRole: Role) extends Service {
  def login(loginDto: LoginDTO): Maybe[JwtClaimsSet] =
    for {
      user <- repository.byUsername(loginDto.username)
      _ <- Hash.compare(raw = loginDto.password, hashed = user.password)
    } yield JwtClaimsSet(defaultClaims(user) ++ customClaims(user))

  protected def defaultClaims(user: T): Map[String, Any] = Map(
    "username" -> user.username,
    "email" -> user.email,
    "role" -> defaultRole.toString
  )

  protected def customClaims(user: T): Map[String, Any] = Map.empty
}
