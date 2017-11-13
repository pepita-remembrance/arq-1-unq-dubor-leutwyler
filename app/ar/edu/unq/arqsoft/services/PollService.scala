package ar.edu.unq.arqsoft.services

import javax.inject.{Inject, Singleton}

import ar.edu.unq.arqsoft.DAOs.{PollDAO, SubjectDAO}
import ar.edu.unq.arqsoft.api.{CreatePollDTO, PollDTO}
import ar.edu.unq.arqsoft.model.{Career, Poll}
import ar.edu.unq.arqsoft.database.DSLFlavor._

@Singleton
class PollService @Inject()(pollDAO: PollDAO, subjectDAO: SubjectDAO) extends Service[Poll] {

  def create(career: Career, dto: CreatePollDTO): PollDTO = inTransaction {
    val newPoll = asModel(career, dto)
    pollDAO.save(newPoll)
    dto.offer.foreach(_.foreach {
      case (subjectShortName, optionsDTO) =>
        val subject = subjectDAO.search(_.careerId === career.id, _.shortName === subjectShortName).single
        val (courses, nonCourses) = optionsDTO.map(_.asModel).partition(_.isCourse)

      //TODO Crear la oferta!
    })
    newPoll
  }

  def asModel(career: Career, dto: CreatePollDTO): Poll =
    Poll(dto.key, career.id, isOpen = true)

}
