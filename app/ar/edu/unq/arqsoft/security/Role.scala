package ar.edu.unq.arqsoft.security

sealed abstract class Role {
  def matches(other: Role): Boolean
}

sealed abstract class RoleFactory(val jwtRoleKey: String) extends Role {
  Role.register(this)

  def apply(fileNumber: Int): Role

  def apply(fileNumber: Option[Int]): Role =
    fileNumber.map(this (_)).getOrElse(this)
}

sealed abstract class RoleConcrete extends Role

case class RoleStudent(fileNumber: Int) extends RoleConcrete {
  def matches(other: Role): Boolean = other match {
    case RoleStudent(n) if n == fileNumber => true
    case _ => false
  }
}

object RoleStudent extends RoleFactory("student") {
  def matches(other: Role): Boolean = other match {
    case RoleStudent(_) | RoleStudent => true
    case _ => false
  }
}

case class RoleAdmin(fileNumber: Int) extends RoleConcrete {
  def matches(other: Role): Boolean = other match {
    case RoleAdmin(n) if n == fileNumber => true
    case _ => false
  }
}

object RoleAdmin extends RoleFactory("admin") {
  def matches(other: Role): Boolean = other match {
    case RoleAdmin(_) | RoleAdmin => true
    case _ => false
  }
}

object Role {
  private var roles: List[RoleFactory] = List.empty

  private[security] def register(role: RoleFactory): Unit =
    roles :+= role

  def fromJWTKey(role: String): Option[RoleFactory] =
    roles.find(_.jwtRoleKey == role)
}
