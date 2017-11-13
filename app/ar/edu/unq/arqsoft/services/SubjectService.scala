package ar.edu.unq.arqsoft.services

import javax.inject.{Inject, Singleton}

import ar.edu.unq.arqsoft.DAOs.SubjectDAO
import ar.edu.unq.arqsoft.api.{CreateSubjectDTO, SubjectDTO}
import ar.edu.unq.arqsoft.model.{Career, Subject}

@Singleton
class SubjectService @Inject()(subjectDAO: SubjectDAO) extends Service[Subject] {

  def create(career: Career, dto: CreateSubjectDTO): SubjectDTO = inTransaction {
    val newSubject = dto.asModel(career)
    subjectDAO.save(newSubject)
    newSubject
  }

  def create(career: Career, dtos: Iterable[CreateSubjectDTO]): Iterable[SubjectDTO] = inTransaction{
    val newSubjects = dtos.map(_.asModel(career))
    subjectDAO.save(newSubjects)
    newSubjects.mapAs[SubjectDTO]
  }
}
