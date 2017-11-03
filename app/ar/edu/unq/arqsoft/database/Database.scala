package ar.edu.unq.arqsoft.database

import org.joda.time.DateTimeZone

object Database extends ToyDatabase with H2Connector

trait Database extends InscriptionPollSchema {
  this: DBConnector =>

  DateTimeZone.setDefault(DateTimeZone.forOffsetHours(-3)) // Buenos Aires
}

trait ToyDatabase extends Database with InscriptionPollHelpers {
  this: DBConnector =>
  seed()
}

trait InscriptionPollHelpers extends Database {
  this: DBConnector =>
  def seed() = inTransaction {
    drop
    create
  }
}