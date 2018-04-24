import ar.edu.unq.arqsoft.database.{DBConnector, H2Connector}
import net.codingwell.scalaguice.ScalaModule

class Module extends ScalaModule
  with DatabaseBindings

trait DatabaseBindings {
  this: ScalaModule =>
  def configure(): Unit = {
    bind[DBConnector].to[H2Connector]
  }
}
