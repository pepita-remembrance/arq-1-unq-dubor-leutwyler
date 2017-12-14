package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api._
import com.google.inject.Singleton

@Singleton
class AdminService extends Service with AdminCareerService {

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

  def joinCareer(dto: CreateAdminCareerDTO): AdminDTO = inTransaction {
    createAdminCareer(dto)._1
  }

  def careers(fileNumber: Int): Iterable[PartialCareerForAdminDTO] = inTransaction {
    CareerDAO.careersOfAdmin(AdminCareerDAO.whereAdmin(AdminDAO.whereFileNumber(fileNumber))).mapAs[PartialCareerForAdminDTO]
  }

  def polls(fileNumber: Int): Iterable[PartialPollForAdminDTO] = inTransaction {
    PollDAO.pollsOfAdmin(AdminCareerDAO.whereAdmin(AdminDAO.whereFileNumber(fileNumber))).mapAs[PartialPollForAdminDTO]
  }
}