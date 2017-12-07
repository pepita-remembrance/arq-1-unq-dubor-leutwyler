package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.api.{CreateCareerDTO, CreateStudentCareerDTO}
import ar.edu.unq.arqsoft.mappings.json.PlayJsonDTOFormats
import ar.edu.unq.arqsoft.services.{CareerService, StudentService}
import com.google.inject.{Inject, Singleton}
import play.api.mvc.{ControllerComponents, PlayBodyParsers, Request}

@Singleton
class CareerController @Inject()(cc: ControllerComponents, parse: PlayBodyParsers,
                                 studentService: StudentService,
                                 careerService: CareerService
                                )
  extends BasicController(cc, parse) with PlayJsonDTOFormats {

  def create = JsonActionWithBody[CreateCareerDTO] {
    implicit request: Request[CreateCareerDTO] =>
      careerService.create(request.body)
  }

  def all = JsonAction {
    careerService.all
  }

  def get(careerShortName: String) = JsonAction {
    careerService.byShortName(careerShortName)
  }

  def createStudentCareer = JsonActionWithBody[CreateStudentCareerDTO] {
    implicit request: Request[CreateStudentCareerDTO] =>
      careerService.joinStudent(request.body)
  }

}
