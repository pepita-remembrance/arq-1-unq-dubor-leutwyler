package ar.edu.unq.arqsoft.security

sealed abstract class Role(val stringValue: String) {
  Role.register(this)
}

object Role {

  private var roles: List[Role] = List.empty

  private[security] def register(role: Role): Unit =
    roles :+= role

  def fromString(role: String): Option[Role] =
    roles.find(_.stringValue == role)

  case object Student extends Role("student")

  case object Admin extends Role("admin")

  case object AnyRole extends Role("anyRole")

}
