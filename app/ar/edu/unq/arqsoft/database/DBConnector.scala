package ar.edu.unq.arqsoft.database

import java.sql.DriverManager

import com.google.inject.{Inject, Singleton}
import org.squeryl.adapters.PostgreSqlAdapter
import org.squeryl.{AbstractSession, Session}
import play.api.Configuration

trait DBConnector {
  def sessionCreator: Option[() => AbstractSession]
}

@Singleton
class PostgresConnector @Inject()(configuration: Configuration) extends DBConnector {
  def sessionCreator: Option[() => AbstractSession] = {
    Some(() => Session.create(
      DriverManager.getConnection(configuration.get[String]("app.database.JDBCurl")),
      new PostgreSqlAdapter
    ))
  }
}