package ar.edu.unq.arqsoft.services

import javax.inject.{Inject, Singleton}

import ar.edu.unq.arqsoft.DAOs.StudentDAO
import ar.edu.unq.arqsoft.api.{CreateStudentDTO, PartialStudentDTO, StudentDTO}
import ar.edu.unq.arqsoft.model.Student

@Singleton
class StudentService @Inject()(studentDAO: StudentDAO) extends Service[Student] {

  def create(dto: CreateStudentDTO): StudentDTO = inTransaction {
    val newStudent = dto.asModel
    studentDAO.save(newStudent)
    newStudent
  }

  def all: Iterable[PartialStudentDTO] = inTransaction {
    studentDAO.all.mapAs[PartialStudentDTO]
  }

}
