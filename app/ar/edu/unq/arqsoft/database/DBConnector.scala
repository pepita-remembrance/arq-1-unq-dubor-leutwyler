package ar.edu.unq.arqsoft.database

import java.sql.DriverManager

import org.squeryl.{AbstractSession, Session, SessionFactory}
import org.squeryl.adapters.H2Adapter

trait DBConnector {
  def sessionCreator: Option[() => AbstractSession]
}

trait H2Connector extends DBConnector {
  def sessionCreator: Option[() => AbstractSession] = Some(() => Session.create(
    DriverManager.getConnection("jdbc:h2:~/test?user=h2&password=test"),
    new H2Adapter
  ))
}
