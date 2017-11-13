package ar.edu.unq.arqsoft.model

import ar.edu.unq.arqsoft.database.InscriptionPollSchema
import ar.edu.unq.arqsoft.model.TableRow.KeyType

case class Student(fileNumber: Int, email: String, name: String, surname: String) extends TableRow {
  lazy val careers = InscriptionPollSchema.studentsCareers.left(this)
  lazy val results = InscriptionPollSchema.studentResults.left(this)
}

case class StudentCareer(studentId: KeyType, careerId: KeyType) extends TableRow