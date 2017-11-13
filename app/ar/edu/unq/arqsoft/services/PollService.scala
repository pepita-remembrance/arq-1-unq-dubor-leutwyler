package ar.edu.unq.arqsoft.services

import javax.inject.{Inject, Singleton}

import ar.edu.unq.arqsoft.DAOs.PollDAO
import ar.edu.unq.arqsoft.api.{CreatePollDTO, PollDTO}
import ar.edu.unq.arqsoft.model.{Career, Poll}

@Singleton
class PollService @Inject()(pollDAO: PollDAO) extends Service[Poll] {

  def create(career: Career, dto: CreatePollDTO): PollDTO = inTransaction {
    val newPoll = asModel(career, dto)
    pollDAO.save(newPoll)
    //TODO Crear la oferta!
    newPoll
  }

  def asModel(career: Career, dto: CreatePollDTO): Poll =
    Poll(dto.key, career.id, isOpen = true)

}
