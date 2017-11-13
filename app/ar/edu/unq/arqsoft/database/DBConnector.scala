package ar.edu.unq.arqsoft.database

import java.sql.DriverManager
import javax.inject.Singleton

import org.squeryl.adapters.H2Adapter
import org.squeryl.{AbstractSession, Session}

trait DBConnector {
  def sessionCreator: Option[() => AbstractSession]
}

@Singleton
class H2Connector extends DBConnector {
  def sessionCreator: Option[() => AbstractSession] = Some(() => Session.create(
    DriverManager.getConnection("jdbc:h2:~/test?user=h2&password=test"),
    new H2Adapter
  ))
}
