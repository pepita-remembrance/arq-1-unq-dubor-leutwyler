package ar.edu.unq.arqsoft.controllers

import javax.inject._

import ar.edu.unq.arqsoft.api.{CreateStudentDTO, PartialStudentDTO}
import ar.edu.unq.arqsoft.mappings.json.PlayJsonDTOFormats
import ar.edu.unq.arqsoft.services.StudentService
import play.api.libs.json.Json
import play.api.mvc._

@Singleton
class StudentController @Inject()(cc: ControllerComponents, parse: PlayBodyParsers, studentService: StudentService)
  extends BasicController(cc, parse) with PlayJsonDTOFormats {

  def create: Action[CreateStudentDTO] = Action(validateJson[CreateStudentDTO]) { implicit request: Request[CreateStudentDTO] =>
    Ok(Json.toJson(studentService.create(request.body)))
  }

  def all = Action {
    Ok(Json.toJson(studentService.all))
  }

}
