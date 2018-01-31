package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api._
import ar.edu.unq.arqsoft.maybe.Maybe
import com.google.inject.Singleton

@Singleton
class AdminService extends Service {

  def create(dto: CreateAdminDTO): Maybe[AdminDTO] = inTransaction {
    val newAdmin = dto.asModel
    AdminDAO.save(newAdmin)
    newAdmin
  }

  def all: Maybe[Iterable[PartialAdminDTO]] = inTransaction {
    AdminDAO.all.mapAs[PartialAdminDTO]
  }

  def byFileNumber(fileNumber: Int): Maybe[AdminDTO] = inTransaction {
    AdminDAO.whereFileNumber(fileNumber).single
  }

  def careers(fileNumber: Int): Maybe[Iterable[PartialCareerForAdminDTO]] = inTransaction {
    CareerDAO.careersOfAdmin(fileNumber).mapAs[PartialCareerForAdminDTO]
  }

  def polls(fileNumber: Int): Maybe[Iterable[PartialPollForAdminDTO]] = inTransaction {
    PollDAO.pollsOfAdmin(fileNumber).mapAs[PartialPollForAdminDTO]
  }
}