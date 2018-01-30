package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api._
import com.google.inject.Singleton

@Singleton
class AdminService extends Service {

  def create(dto: CreateAdminDTO): AdminDTO = inTransaction {
    val newAdmin = dto.asModel
    AdminDAO.save(newAdmin)
    newAdmin
  }

  def all: Iterable[PartialAdminDTO] = inTransaction {
    AdminDAO.all.mapAs[PartialAdminDTO]
  }

  def byFileNumber(fileNumber: Int): AdminDTO = inTransaction {
    AdminDAO.whereFileNumber(fileNumber).single
  }

  def careers(fileNumber: Int): Iterable[PartialCareerForAdminDTO] = inTransaction {
    CareerDAO.careersOfAdmin(fileNumber).mapAs[PartialCareerForAdminDTO]
  }

  def polls(fileNumber: Int): Iterable[PartialPollForAdminDTO] = inTransaction {
    PollDAO.pollsOfAdmin(fileNumber).mapAs[PartialPollForAdminDTO]
  }
}