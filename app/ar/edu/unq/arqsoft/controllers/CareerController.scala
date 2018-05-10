package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.api.{CreateAdminCareerDTO, CreateCareerDTO, CreateStudentCareerDTO}
import ar.edu.unq.arqsoft.mappings.json.PlayJsonDTOFormats
import ar.edu.unq.arqsoft.security.{JWTService, Role}
import ar.edu.unq.arqsoft.services.{CareerService, StudentService}
import com.google.inject.{Inject, Singleton}
import play.api.mvc.{ControllerComponents, PlayBodyParsers, Request}

@Singleton
class CareerController @Inject()(cc: ControllerComponents, parse: PlayBodyParsers,
                                 jwtService: JWTService,
                                 studentService: StudentService,
                                 careerService: CareerService
                                )
  extends BasicController(cc, parse, jwtService) with PlayJsonDTOFormats {

  def create = JsonAction.withBody[CreateCareerDTO].requires(Role.Admin) {
    implicit request: Request[CreateCareerDTO] =>
      careerService.create(request.body)
  }

  def all = JsonAction.requires(Role.Admin) {
    careerService.all
  }

  def get(careerShortName: String) = JsonAction.requires(Role.Student, Role.Admin) {
    careerService.byShortName(careerShortName)
  }

  def createStudentCareer = JsonAction.withBody[CreateStudentCareerDTO].requires(Role.Admin) {
    implicit request: Request[CreateStudentCareerDTO] =>
      careerService.joinStudent(request.body)
  }

  def createAdminCareer = JsonAction.withBody[CreateAdminCareerDTO].requires(Role.Admin) {
    implicit request: Request[CreateAdminCareerDTO] =>
      careerService.joinAdmin(request.body)
  }

}
