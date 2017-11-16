package ar.edu.unq.arqsoft.services

import com.google.inject.{Inject, Singleton}

import ar.edu.unq.arqsoft.DAOs.{CareerDAO, StudentDAO, SubjectDAO}
import ar.edu.unq.arqsoft.api.{CareerDTO, CreateCareerDTO, PartialCareerDTO}

@Singleton
class CareerService @Inject()(careerDAO: CareerDAO, subjectDAO: SubjectDAO, studentDAO: StudentDAO)
  extends Service with StudentCareerService{

  def create(dto: CreateCareerDTO): CareerDTO = inTransaction {
    val newCareer = dto.asModel
    careerDAO.save(newCareer)
    dto.subjects.foreach(subjects => subjectDAO.save(subjects.map(_.asModel(newCareer))))
    newCareer
  }

  def all: Iterable[PartialCareerDTO] = inTransaction {
    careerDAO.all.mapAs[PartialCareerDTO]
  }

  def joinStudent(studentFileNumber: Int, careerShortName: String): CareerDTO = inTransaction {
    val student = studentDAO.whereFileNumber(studentFileNumber).single
    val career = careerDAO.whereShortName(careerShortName).single
    joinCareer(student, career)
    career
  }
}
