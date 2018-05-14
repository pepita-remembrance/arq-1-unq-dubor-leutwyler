package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.api.CreateAdminDTO
import ar.edu.unq.arqsoft.mappings.json.PlayJsonDTOFormats
import ar.edu.unq.arqsoft.security.{JWTService, Role}
import ar.edu.unq.arqsoft.services.AdminService
import com.google.inject.{Inject, Singleton}
import play.api.mvc.{ControllerComponents, PlayBodyParsers, Request}


@Singleton
class AdminController @Inject()(cc: ControllerComponents,
                                parse: PlayBodyParsers,
                                adminService: AdminService,
                                jwtService: JWTService
                               )
  extends BasicController(cc, parse, jwtService) with PlayJsonDTOFormats {

  def create = JsonAction.withBody[CreateAdminDTO].requires(Role.Admin) {
    implicit request: Request[CreateAdminDTO] =>
      adminService.create(request.body)
  }

  def all = JsonAction.requires(Role.Admin) {
    adminService.all
  }

  def get(fileNumber: Int) = JsonAction.requires(Role.Admin) {
    adminService.byFileNumber(fileNumber)
  }

  def careers(fileNumber: Int) = JsonAction.requires(Role.Admin) {
    adminService.careers(fileNumber)
  }

  def polls(filenumber: Int) = JsonAction.requires(Role.Admin) {
    adminService.polls(filenumber)
  }

}
