package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api._
import com.google.inject.Singleton

@Singleton
class StudentService extends Service {

  def create(dto: CreateStudentDTO): StudentDTO = inTransaction {
    val newStudent = dto.asModel
    StudentDAO.save(newStudent)
    newStudent
  }

  def all: Iterable[PartialStudentDTO] = inTransaction {
    StudentDAO.all.mapAs[PartialStudentDTO]
  }

  def byFileNumber(fileNumber: Int): StudentDTO = inTransaction {
    StudentDAO.whereFileNumber(fileNumber).single
  }

}
