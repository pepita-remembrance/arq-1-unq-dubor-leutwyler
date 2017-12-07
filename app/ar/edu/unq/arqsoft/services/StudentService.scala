package ar.edu.unq.arqsoft.services

import com.google.inject.{Inject, Singleton}
import ar.edu.unq.arqsoft.DAOs.{CareerDAO, StudentDAO}
import ar.edu.unq.arqsoft.api._

@Singleton
class StudentService @Inject()(val studentDAO: StudentDAO, val careerDAO: CareerDAO)
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

  def joinCareer(dto: CreateStudentCareerDTO): StudentDTO = inTransaction {
    createStudentCareer(dto)._1
  }

}
