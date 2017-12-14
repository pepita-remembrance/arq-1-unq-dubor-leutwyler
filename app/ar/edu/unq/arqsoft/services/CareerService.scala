package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api._
import com.google.inject.Singleton
import org.joda.time.DateTime

@Singleton
class CareerService
  extends Service with StudentCareerService with AdminCareerService {

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
    createStudentCareer(dto, joinDate)._2
  }

  def joinAdmin(dto: CreateAdminCareerDTO, joinDate:DateTime=DateTime.now): CareerDTO = inTransaction {
    createAdminCareer(dto, joinDate)._2
  }

}
