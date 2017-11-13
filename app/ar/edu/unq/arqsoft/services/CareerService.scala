package ar.edu.unq.arqsoft.services

import javax.inject.{Inject, Singleton}

import ar.edu.unq.arqsoft.DAOs.CareerDAO
import ar.edu.unq.arqsoft.api.{CareerDTO, CreateCareerDTO, PartialCareerDTO}
import ar.edu.unq.arqsoft.model.Career

@Singleton
class CareerService @Inject()(careerDAO: CareerDAO, subjectService: SubjectService) extends Service[Career] {

  def create(dto: CreateCareerDTO): CareerDTO = inTransaction {
    val newCareer = dto.asModel
    careerDAO.save(newCareer)
    dto.subjects.map(subjectService.create(newCareer, _))
    newCareer
  }

  def all: Iterable[PartialCareerDTO] = inTransaction {
    careerDAO.all.mapAs[PartialCareerDTO]
  }
}
