package ar.edu.unq.arqsoft.services

import javax.inject.{Inject, Singleton}

import ar.edu.unq.arqsoft.DAOs.StudentDAO
import ar.edu.unq.arqsoft.api.{CreateStudentDTO, PartialStudentDTO}
import ar.edu.unq.arqsoft.database.Database.inTransaction
import ar.edu.unq.arqsoft.mappings.dto.DTOMappings
import ar.edu.unq.arqsoft.model.Student

@Singleton
class StudentService @Inject()(studentDao: StudentDAO) extends DTOMappings {

  def create(dto: CreateStudentDTO): PartialStudentDTO = inTransaction {
    val newStudent = Student(dto.fileNumber, dto.email, dto.name, dto.surname)
    studentDao.save(newStudent)
  }

  def all:Iterable[PartialStudentDTO] = inTransaction {
    studentDao.all.mapAs[PartialStudentDTO]
  }

}
