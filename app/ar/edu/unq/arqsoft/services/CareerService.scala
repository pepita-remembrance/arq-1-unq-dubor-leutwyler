package ar.edu.unq.arqsoft.services

import javax.inject.{Inject, Singleton}

import ar.edu.unq.arqsoft.DAOs.CareerDAO
import ar.edu.unq.arqsoft.api.{CareerDTO, CreateCareerDTO, PartialCareerDTO}
import ar.edu.unq.arqsoft.database.DSLFlavor.inTransaction
import ar.edu.unq.arqsoft.mappings.dto.OutputDTOMappings
import ar.edu.unq.arqsoft.model.Career

@Singleton
class CareerService @Inject()(careerDAO: CareerDAO, subjectService: SubjectService) extends OutputDTOMappings {

  def create(dto: CreateCareerDTO): CareerDTO = inTransaction {
    val newCareer = asModel(dto)
    careerDAO.save(newCareer)
    dto.subjects.map(subjectService.create(newCareer, _))
    newCareer
  }

  def all: Iterable[PartialCareerDTO] = inTransaction {
    careerDAO.all.mapAs[PartialCareerDTO]
  }

  def asModel(dto: CreateCareerDTO): Career =
    Career(dto.shortName, dto.longName)

}
