package ar.edu.unq.arqsoft.database

import ar.edu.unq.arqsoft.model.Student
import org.joda.time.DateTimeZone
import org.squeryl.SessionFactory

object Database extends ToyDatabase with H2Connector

trait Database extends InscriptionPollSchema {
  this: DBConnector =>

  DateTimeZone.setDefault(DateTimeZone.forOffsetHours(-3)) // Buenos Aires
  SessionFactory.concreteFactory = sessionCreator
}

trait ToyDatabase extends Database with InscriptionPollHelpers {
  this: DBConnector =>
  seed()
}

trait InscriptionPollHelpers extends Database {
  this: DBConnector =>

  def init() = inTransaction {
    drop
    create
  }

  def seed() = inTransaction {
    init()
    students.insert(List(
      Student(123,"123@asd.com","asd","dsa"),
      Student(456,"456@asd.com","asd","dsa"),
      Student(789,"789@asd.com","asd","dsa")
    ))
  }
}