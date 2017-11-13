package ar.edu.unq.arqsoft.model

import ar.edu.unq.arqsoft.database.Database
import ar.edu.unq.arqsoft.model.TableRow.KeyType

case class Student(fileNumber: Int, email: String, name: String, surname: String) extends TableRow {
  lazy val careers = Database.studentsCareers.left(this)
  lazy val results = Database.studentResults.left(this)
}

case class StudentCareer(studentId: KeyType, careerId: KeyType) extends TableRow