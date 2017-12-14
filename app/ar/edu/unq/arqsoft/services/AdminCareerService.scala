package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api.CreateAdminCareerDTO
import ar.edu.unq.arqsoft.model.{Admin, AdminCareer, Career}

trait AdminCareerService extends Service {
  this: Service =>

  protected def createAdminCareer(dto: CreateAdminCareerDTO): (Admin, Career) = inTransaction {
    val admin = AdminDAO.whereFileNumber(dto.adminFileNumber).single
    val career = CareerDAO.whereShortName(dto.careerShortName).single
    AdminCareerDAO.save(AdminCareer(admin.id, career.id))
    (admin, career)
  }

}
