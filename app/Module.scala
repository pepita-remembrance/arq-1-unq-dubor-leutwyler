import ar.edu.unq.arqsoft.database.{DBConnector, PostgresConnector}
import net.codingwell.scalaguice.ScalaModule

class Module extends ScalaModule
  with DatabaseBindings

trait DatabaseBindings {
  this: ScalaModule =>
  def configure(): Unit = {
    bind[DBConnector].to[PostgresConnector]
  }
}
