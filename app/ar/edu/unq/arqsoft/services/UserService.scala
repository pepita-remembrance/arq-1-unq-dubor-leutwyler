package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api.LoginDTO
import ar.edu.unq.arqsoft.maybe.Maybe
import ar.edu.unq.arqsoft.model.{TableRow, User}
import ar.edu.unq.arqsoft.repository.UserRepository
import ar.edu.unq.arqsoft.utils.Hash
import authentikat.jwt.JwtClaimsSet

abstract class UserService[T <: User with TableRow](repository: UserRepository[T]) extends Service {
  def login(loginDto: LoginDTO): Maybe[JwtClaimsSet] =
    for {
      user <- repository.byUsername(loginDto.username)
      _ <- Hash.compare(raw = loginDto.password, hashed = user.password)
    } yield makeClaimsSet(user)

  protected def makeClaimsSet(user: T): JwtClaimsSet
}
