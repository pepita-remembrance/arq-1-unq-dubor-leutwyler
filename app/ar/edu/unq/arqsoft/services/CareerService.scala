package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api.{CareerDTO, CreateCareerDTO, CreateStudentCareerDTO, PartialCareerDTO}
import com.google.inject.Singleton

@Singleton
class CareerService
  extends Service with StudentCareerService {

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

  def joinStudent(dto: CreateStudentCareerDTO): CareerDTO = inTransaction {
    createStudentCareer(dto)._2
  }

}
