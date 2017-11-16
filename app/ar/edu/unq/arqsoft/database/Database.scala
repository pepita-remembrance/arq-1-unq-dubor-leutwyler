package ar.edu.unq.arqsoft.database

import javax.inject.{Inject, Singleton}

import org.joda.time.DateTimeZone
import org.squeryl.SessionFactory

@Singleton
class Database @Inject()(connector: DBConnector) extends DemoDatabase with SeedData {

  DateTimeZone.setDefault(DateTimeZone.forOffsetHours(-3)) // Buenos Aires
  SessionFactory.concreteFactory = connector.sessionCreator
  init()

  override def seed(): Unit = {
    init()
    super.seed()
  }
}

trait DemoDatabase {
  def init(): Unit = DSLFlavor.inTransaction {
    InscriptionPollSchema.drop
    InscriptionPollSchema.create
  }
}
