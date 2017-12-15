package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.api.{CreateAdminCareerDTO, CreateAdminDTO}
import ar.edu.unq.arqsoft.mappings.json.PlayJsonDTOFormats
import ar.edu.unq.arqsoft.services.{AdminService}
import com.google.inject.{Inject, Singleton}
import play.api.mvc.{ControllerComponents, PlayBodyParsers, Request}


@Singleton
class AdminController @Inject()(cc: ControllerComponents, parse: PlayBodyParsers,
                                adminService: AdminService,
                               )
  extends BasicController(cc, parse) with PlayJsonDTOFormats {

  def create = JsonAction withBody[CreateAdminDTO] {
    implicit request: Request[CreateAdminDTO] =>
      adminService.create(request.body)
  }

  def all = JsonAction {
    adminService.all
  }

  def get(fileNumber: Int) = JsonAction {
    adminService.byFileNumber(fileNumber)
  }

  def createAdminCareer = JsonAction withBody[CreateAdminCareerDTO] {
    implicit request: Request[CreateAdminCareerDTO] =>
      adminService.joinCareer(request.body)
  }

  def careers(fileNumber: Int) = JsonAction {
    adminService.careers(fileNumber)
  }

  def polls(filenumber: Int) = JsonAction {
    adminService.polls(filenumber)
  }

}
