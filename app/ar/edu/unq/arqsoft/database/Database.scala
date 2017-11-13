package ar.edu.unq.arqsoft.database

import javax.inject.{Inject, Singleton}

import ar.edu.unq.arqsoft.api.CreateStudentDTO
import ar.edu.unq.arqsoft.services.StudentService
import org.joda.time.DateTimeZone
import org.squeryl.SessionFactory

@Singleton
class Database @Inject()(connector: DBConnector) extends DemoDatabase with SeedData {

  DateTimeZone.setDefault(DateTimeZone.forOffsetHours(-3)) // Buenos Aires
  SessionFactory.concreteFactory = connector.sessionCreator
  init()
}

trait DemoDatabase {
  def init(): Unit = DSLFlavor.inTransaction {
    InscriptionPollSchema.drop
    InscriptionPollSchema.create
  }
}

trait SeedData {
  @Inject
  var studentService: StudentService = _

  def seed(): Unit = {
    List(
      CreateStudentDTO(123, "marcogomez@gmail.com", "Marco", "Gomez"),
      CreateStudentDTO(456, "joaquinsanchez@gmail.com", "Joaquin", "Sanchez")
    ).map(studentService.create(_))
    //    List(
    //      Career("TPI", "Tecnicatura Universitaria en Programacion Informatica")
    //    )
  }
}