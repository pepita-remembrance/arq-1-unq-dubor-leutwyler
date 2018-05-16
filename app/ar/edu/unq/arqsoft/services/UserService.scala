package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api.{LoginDTO, UserDTO}
import ar.edu.unq.arqsoft.maybe.Maybe
import ar.edu.unq.arqsoft.model.{TableRow, User}
import ar.edu.unq.arqsoft.repository.UserRepository
import ar.edu.unq.arqsoft.security.RoleFactory
import ar.edu.unq.arqsoft.utils.Hash
import authentikat.jwt.JwtClaimsSet

abstract class UserService[T <: User with TableRow](repository: UserRepository[T], defaultRole: RoleFactory) extends Service {
  protected def toDTO(user: T): UserDTO

  def login(loginDto: LoginDTO): Maybe[(UserDTO, JwtClaimsSet)] =
    for {
      user <- repository.byUsername(loginDto.username)
      _ <- Hash.compare(raw = loginDto.password, hashed = user.password)
    } yield (toDTO(user), JwtClaimsSet(defaultClaims(user) ++ customClaims(user)))

  protected def defaultClaims(user: User): Map[String, Any] = Map(
    "username" -> user.username,
    "email" -> user.email,
    "role" -> defaultRole.jwtRoleKey
  )

  protected def customClaims(user: T): Map[String, Any] = Map.empty
}
