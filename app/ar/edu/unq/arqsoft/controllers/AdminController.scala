package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.api.CreateAdminDTO
import ar.edu.unq.arqsoft.mappings.json.PlayJsonDTOFormats
import ar.edu.unq.arqsoft.security.{JWTService, RoleAdmin}
import ar.edu.unq.arqsoft.services.AdminService
import com.google.inject.Inject
import play.api.mvc.{ControllerComponents, PlayBodyParsers, Request}

class AdminController @Inject()(cc: ControllerComponents,
                                parse: PlayBodyParsers,
                                adminService: AdminService,
                                jwtService: JWTService
                               )
  extends BasicController(cc, parse, jwtService) with PlayJsonDTOFormats {

  def create = JsonAction.withBody[CreateAdminDTO].requires(RoleAdmin) {
    implicit request: Request[CreateAdminDTO] =>
      adminService.create(request.body)
  }

  def all = JsonAction.requires(RoleAdmin) {
    adminService.all
  }

  def get(fileNumber: Int) = JsonAction.requires(RoleAdmin(fileNumber)) {
    adminService.byFileNumber(fileNumber)
  }

  def careers(fileNumber: Int) = JsonAction.requires(RoleAdmin(fileNumber)) {
    adminService.careers(fileNumber)
  }

  def polls(fileNumber: Int) = JsonAction.requires(RoleAdmin(fileNumber)) {
    adminService.polls(fileNumber)
  }

}
