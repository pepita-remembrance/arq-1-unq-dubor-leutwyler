package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api._
import org.joda.time.DateTime
import com.google.inject.Singleton

@Singleton
class AdminService extends Service with AdminCareerService{

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

  def joinCareer(dto: CreateAdminCareerDTO, joinDate:DateTime = DateTime.now): AdminDTO = inTransaction {
    createAdminCareer(dto, joinDate)._1
  }
}