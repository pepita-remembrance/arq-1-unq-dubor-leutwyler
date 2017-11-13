package ar.edu.unq.arqsoft.services

import javax.inject.{Inject, Singleton}

import ar.edu.unq.arqsoft.DAOs.StudentDAO
import ar.edu.unq.arqsoft.api.{CreateStudentDTO, PartialStudentDTO, StudentDTO}
import ar.edu.unq.arqsoft.database.DSLFlavor.inTransaction
import ar.edu.unq.arqsoft.mappings.dto.OutputDTOMappings
import ar.edu.unq.arqsoft.model.Student

@Singleton
class StudentService @Inject()(studentDAO: StudentDAO) extends OutputDTOMappings {

  def create(dto: CreateStudentDTO): StudentDTO = inTransaction {
    val newStudent = asModel(dto)
    studentDAO.save(newStudent)
    newStudent
  }

  def all: Iterable[PartialStudentDTO] = inTransaction {
    studentDAO.all.mapAs[PartialStudentDTO]
  }

  def asModel(dto: CreateStudentDTO): Student =
    Student(dto.fileNumber, dto.email, dto.name, dto.surname)

}
