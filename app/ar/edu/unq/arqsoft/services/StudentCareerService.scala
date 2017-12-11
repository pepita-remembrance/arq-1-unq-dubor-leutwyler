package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api.CreateStudentCareerDTO
import ar.edu.unq.arqsoft.model.{Career, Student, StudentCareer}
import org.joda.time.DateTime

trait StudentCareerService extends Service {
  this: Service =>

  protected def createStudentCareer(dto: CreateStudentCareerDTO): (Student, Career) = inTransaction {
    val student = StudentDAO.whereFileNumber(dto.studentFileNumber).single
    val career = CareerDAO.whereShortName(dto.careerShortName).single
    StudentCareerDAO.save(StudentCareer(student.id, career.id, dto.joinDate.getOrElse(DateTime.now)))
    (student, career)
  }

}
