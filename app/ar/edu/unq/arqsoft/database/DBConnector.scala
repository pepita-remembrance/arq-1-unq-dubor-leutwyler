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
    val dbConfig = configuration.get[Configuration]("app.database")
    Some(() => Session.create(
      DriverManager.getConnection(s"jdbc:postgresql://${dbConfig.get[String]("host")}/${dbConfig.get[String]("db")}?user=${dbConfig.get[String]("user")}&password=${dbConfig.get[String]("password")}"),
      new PostgreSqlAdapter
    ))
  }
}