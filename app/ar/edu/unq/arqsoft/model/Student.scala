package ar.edu.unq.arqsoft.model

import ar.edu.unq.arqsoft.model.TableRow.KeyType

case class Student(fileNumber: Int, name: String, surname: String) extends TableRow

case class StudentCareer(studentId: KeyType, careerId: KeyType) extends TableRow