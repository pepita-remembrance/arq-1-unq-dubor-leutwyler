package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api._
import ar.edu.unq.arqsoft.model.{AdminCareer, StudentCareer}
import com.google.inject.Singleton
import org.joda.time.DateTime

@Singleton
class CareerService
  extends Service {

  def create(dto: CreateCareerDTO): CareerDTO = inTransaction {
    val newCareer = dto.asModel
    CareerDAO.save(newCareer)
    dto.subjects.foreach(subjects => SubjectDAO.save(subjects.map(_.asModel(newCareer))))
    newCareer
  }

  def all: Iterable[PartialCareerDTO] = inTransaction {
    CareerDAO.all.mapAs[PartialCareerDTO]
  }

  def byShortName(shortName: String): CareerDTO = inTransaction {
    CareerDAO.whereShortName(shortName).single
  }

  def joinStudent(dto: CreateStudentCareerDTO, joinDate:DateTime=DateTime.now): CareerDTO = inTransaction {
    val student = StudentDAO.whereFileNumber(dto.studentFileNumber).single
    val career = CareerDAO.whereShortName(dto.careerShortName).single
    StudentCareerDAO.save(StudentCareer(student.id, career.id, joinDate))
    career
  }

  def joinAdmin(dto: CreateAdminCareerDTO): CareerDTO = inTransaction {
    val admin = AdminDAO.whereFileNumber(dto.adminFileNumber).single
    val career = CareerDAO.whereShortName(dto.careerShortName).single
    AdminCareerDAO.save(AdminCareer(admin.id, career.id))
    career
  }

}
