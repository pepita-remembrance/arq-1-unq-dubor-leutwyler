package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.api.{CreateAdminCareerDTO, CreateCareerDTO, CreateStudentCareerDTO}
import ar.edu.unq.arqsoft.mappings.json.PlayJsonDTOFormats
import ar.edu.unq.arqsoft.security.{JWTService, RoleAdmin, RoleStudent}
import ar.edu.unq.arqsoft.services.{CareerService, StudentService}
import com.google.inject.Inject
import play.api.mvc.{ControllerComponents, PlayBodyParsers, Request}

class CareerController @Inject()(cc: ControllerComponents, parse: PlayBodyParsers,
                                 jwtService: JWTService,
                                 studentService: StudentService,
                                 careerService: CareerService
                                )
  extends BasicController(cc, parse, jwtService) with PlayJsonDTOFormats {

  def create = JsonAction.withBody[CreateCareerDTO].requires(RoleAdmin) {
    implicit request: Request[CreateCareerDTO] =>
      careerService.create(request.body)
  }

  def all = JsonAction.requires(RoleAdmin) {
    careerService.all
  }

  def get(careerShortName: String) = JsonAction.requires(RoleStudent, RoleAdmin) {
    careerService.byShortName(careerShortName)
  }

  def createStudentCareer = JsonAction.withBody[CreateStudentCareerDTO].requires(RoleAdmin) {
    implicit request: Request[CreateStudentCareerDTO] =>
      careerService.joinStudent(request.body)
  }

  def createAdminCareer = JsonAction.withBody[CreateAdminCareerDTO].requires(RoleAdmin) {
    implicit request: Request[CreateAdminCareerDTO] =>
      careerService.joinAdmin(request.body)
  }

}
