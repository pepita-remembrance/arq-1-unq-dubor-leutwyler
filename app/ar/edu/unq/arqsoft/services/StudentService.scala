package ar.edu.unq.arqsoft.services

import javax.inject.{Inject, Singleton}

import ar.edu.unq.arqsoft.DAOs.{CareerDAO, StudentDAO}
import ar.edu.unq.arqsoft.api.{CreateStudentDTO, PartialStudentDTO, StudentDTO}

@Singleton
class StudentService @Inject()(studentDAO: StudentDAO, careerDAO: CareerDAO)
  extends Service with StudentCareerService {

  def create(dto: CreateStudentDTO): StudentDTO = inTransaction {
    val newStudent = dto.asModel
    studentDAO.save(newStudent)
    newStudent
  }

  def all: Iterable[PartialStudentDTO] = inTransaction {
    studentDAO.all.mapAs[PartialStudentDTO]
  }

  def byFileNumber(fileNumber: Int): StudentDTO = inTransaction {
    studentDAO.whereFileNumber(fileNumber).single
  }

  def joinCareer(studentFileNumber: Int, careerShortName: String): StudentDTO = inTransaction {
    val student = studentDAO.whereFileNumber(studentFileNumber).single
    val career = careerDAO.whereShortName(careerShortName).single
    joinCareer(student, career)
    student
  }

}
