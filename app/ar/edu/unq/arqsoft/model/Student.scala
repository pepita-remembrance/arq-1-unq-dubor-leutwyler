package ar.edu.unq.arqsoft.model

import ar.edu.unq.arqsoft.database.InscriptionPollSchema
import ar.edu.unq.arqsoft.model.TableRow.KeyType
import org.joda.time.DateTime

case class Student(username: String, password: String, fileNumber: Int, email: String, name: String, surname: String)
  extends User with TableRow {
  lazy val careers = InscriptionPollSchema.studentsCareers.left(this)
  lazy val results = InscriptionPollSchema.studentResults.left(this)
}

case class StudentCareer(studentId: KeyType, careerId: KeyType, joinDate: DateTime) extends TableRow