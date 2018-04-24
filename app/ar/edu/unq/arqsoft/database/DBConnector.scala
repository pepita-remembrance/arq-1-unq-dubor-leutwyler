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
    val databaseUrl = System.getenv("JDBC_DATABASE_URL") match {
      case null =>
        val dbConfig = configuration.get[Configuration]("app.database")
        s"jdbc:postgresql://${dbConfig.get[String]("host")}/${dbConfig.get[String]("db")}?user=${dbConfig.get[String]("user")}&password=${dbConfig.get[String]("password")}"
      case value => value
    }
    Some(() => Session.create(
      DriverManager.getConnection(databaseUrl),
      new PostgreSqlAdapter
    ))
  }
}