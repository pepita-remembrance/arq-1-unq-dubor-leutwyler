package ar.edu.unq.arqsoft.security

object Role extends Enumeration {
  type Role = Value
  val STUDENT = Value("student")
  val ADMIN = Value("admin")
}
