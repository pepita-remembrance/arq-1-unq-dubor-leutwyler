package ar.edu.unq.arqsoft.services

import javax.inject.{Inject, Singleton}

import ar.edu.unq.arqsoft.DAOs.SubjectDAO
import ar.edu.unq.arqsoft.api.{CreateSubjectDTO, SubjectDTO}
import ar.edu.unq.arqsoft.database.Database.inTransaction
import ar.edu.unq.arqsoft.mappings.dto.OutputDTOMappings
import ar.edu.unq.arqsoft.model.{Career, Subject}

@Singleton
class SubjectService @Inject()(subjectDAO: SubjectDAO) extends OutputDTOMappings {

  def create(career: Career, dto: CreateSubjectDTO): SubjectDTO = inTransaction {
    val newSubject = asModel(career, dto)
    subjectDAO.save(newSubject)
    newSubject
  }

  def create(career: Career, dtos: Iterable[CreateSubjectDTO]): Iterable[SubjectDTO] = inTransaction{
    val newSubjects = dtos.map(asModel(career, _))
    subjectDAO.save(newSubjects)
    newSubjects.mapAs[SubjectDTO]
  }

  def asModel(career: Career, dto: CreateSubjectDTO): Subject =
    Subject(career.id, dto.shortName, dto.longName)

}
