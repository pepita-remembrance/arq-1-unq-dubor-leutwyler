package ar.edu.unq.arqsoft.database

import java.sql.DriverManager
import com.google.inject.{Inject, Singleton}

import org.squeryl.adapters.H2Adapter
import org.squeryl.{AbstractSession, Session}
import play.api.Configuration

trait DBConnector {
  def sessionCreator: Option[() => AbstractSession]
}

@Singleton
class H2Connector @Inject()(configuration: Configuration) extends DBConnector {
  def sessionCreator: Option[() => AbstractSession] = {
    val dbConfig = configuration.get[Configuration]("app.database")
    Some(() => Session.create(
      DriverManager.getConnection(s"jdbc:h2:${dbConfig.get[String]("host")}/${dbConfig.get[String]("db")}?user=${dbConfig.get[String]("user")}&password=${dbConfig.get[String]("password")}"),
      new H2Adapter
    ))
  }
}
