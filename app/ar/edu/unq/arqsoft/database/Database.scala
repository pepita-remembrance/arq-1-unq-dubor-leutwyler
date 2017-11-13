package ar.edu.unq.arqsoft.database

import ar.edu.unq.arqsoft.model.{Career, Student}
import org.joda.time.DateTimeZone
import org.squeryl.SessionFactory

object Database extends ToyDatabase with H2Connector

trait Database extends InscriptionPollSchema {
  this: DBConnector =>

  DateTimeZone.setDefault(DateTimeZone.forOffsetHours(-3)) // Buenos Aires
  SessionFactory.concreteFactory = sessionCreator
}

trait ToyDatabase extends Database with SeedData {
  this: DBConnector =>
  def init = inTransaction {
    drop
    create
  }

  init
  seed
}

trait SeedData {

  def seed = {
    List(
      Student(123, "marcogomez@gmail.com", "Marco", "Gomez"),
      Student(456, "joaquinsanchez@gmail.com", "Joaquin", "Sanchez")
    )
    List(
      Career("TPI", "Tecnicatura Universitaria en Programacion Informatica")
    )
  }
}