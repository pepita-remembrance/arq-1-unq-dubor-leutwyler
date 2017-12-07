package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.DAOs.{CareerDAO, StudentDAO, SubjectDAO}
import ar.edu.unq.arqsoft.api.{CareerDTO, CreateCareerDTO, CreateStudentCareerDTO, PartialCareerDTO}
import com.google.inject.{Inject, Singleton}

@Singleton
class CareerService @Inject()(val careerDAO: CareerDAO, subjectDAO: SubjectDAO, val studentDAO: StudentDAO)
  extends Service with StudentCareerService {

  def create(dto: CreateCareerDTO): CareerDTO = inTransaction {
    val newCareer = dto.asModel
    careerDAO.save(newCareer)
    dto.subjects.foreach(subjects => subjectDAO.save(subjects.map(_.asModel(newCareer))))
    newCareer
  }

  def all: Iterable[PartialCareerDTO] = inTransaction {
    careerDAO.all.mapAs[PartialCareerDTO]
  }

  def byShortName(shortName: String): CareerDTO = inTransaction {
    careerDAO.whereShortName(shortName).single
  }

  def joinStudent(dto: CreateStudentCareerDTO): CareerDTO = inTransaction {
    createStudentCareer(dto)._2
  }

}
