package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api.CreateStudentCareerDTO
import ar.edu.unq.arqsoft.model.{Career, Student}

trait StudentCareerService extends Service {
  this: Service =>

  protected def createStudentCareer(dto: CreateStudentCareerDTO): (Student, Career) = inTransaction {
    val student = StudentDAO.whereFileNumber(dto.studentFileNumber).single
    val career = CareerDAO.whereShortName(dto.careerShortName).single
    student.careers.associate(career)
    (student, career)
  }

}
