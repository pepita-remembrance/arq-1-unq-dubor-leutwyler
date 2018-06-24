package ar.edu.unq.arqsoft.database

import akka.actor.ActorSystem
import com.google.inject.{Inject, Singleton}
import org.joda.time.DateTimeZone
import org.squeryl.SessionFactory
import play.api.Configuration

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

@Singleton
class Database @Inject()(connector: DBConnector, configuration: Configuration, actorSystem: ActorSystem)
  extends DemoDatabase
    with SeedData {

  DateTimeZone.setDefault(DateTimeZone.forOffsetHours(-3)) // Buenos Aires
  SessionFactory.concreteFactory = connector.sessionCreator

  actorSystem.scheduler.scheduleOnce(10 seconds) {
    if (configuration.get[Boolean]("db.default.stress.active")) {
      seedForStress(configuration.get[Option[Int]]("db.default.stress.amount"))
    }
  }

  override def seed(): Unit = {
    init()
    super.seed()
  }

  override def seedForStress(amount: Option[Int]): Unit = {
    init()
    super.seedForStress(amount)
  }
}

trait DemoDatabase {
  def init(): Unit = DSLFlavor.inTransaction {
    InscriptionPollSchema.drop
    InscriptionPollSchema.create
  }
}
