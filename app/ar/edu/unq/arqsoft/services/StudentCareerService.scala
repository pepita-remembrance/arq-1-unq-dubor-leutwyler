package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.DAOs.{CareerDAO, StudentDAO}
import ar.edu.unq.arqsoft.api.CreateStudentCareerDTO
import ar.edu.unq.arqsoft.model.{Career, Student}

trait StudentCareerService extends Service {
  this: Service =>

  def studentDAO: StudentDAO

  def careerDAO: CareerDAO

  protected def createStudentCareer(dto: CreateStudentCareerDTO): (Student, Career) = inTransaction {
    val student = studentDAO.whereFileNumber(dto.studentFileNumber).single
    val career = careerDAO.whereShortName(dto.careerShortName).single
    student.careers.associate(career)
    (student, career)
  }

}
