package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api._
import ar.edu.unq.arqsoft.maybe.{Maybe, Something}
import com.google.inject.Singleton

@Singleton
class StudentService extends Service {

  def create(dto: CreateStudentDTO): Maybe[StudentDTO] = inTransaction {
    val newStudent = dto.asModel
    StudentDAO.save(newStudent)
    Something(newStudent)
  }

  def all: Maybe[Iterable[PartialStudentDTO]] = inTransaction {
    StudentDAO.all.mapAs[PartialStudentDTO]
  }

  def byFileNumber(fileNumber: Int): Maybe[StudentDTO] = inTransaction {
    Something(StudentDAO.byFileNumber(fileNumber).single)
  }

}
