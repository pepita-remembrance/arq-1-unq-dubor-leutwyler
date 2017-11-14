package ar.edu.unq.arqsoft.controllers

import javax.inject._

import ar.edu.unq.arqsoft.api.CreateStudentDTO
import ar.edu.unq.arqsoft.mappings.json.PlayJsonDTOFormats
import ar.edu.unq.arqsoft.services.StudentService
import play.api.libs.json.Json
import play.api.mvc._

import scala.util.Try

@Singleton
class StudentController @Inject()(cc: ControllerComponents, parse: PlayBodyParsers, studentService: StudentService)
  extends BasicController(cc, parse) with PlayJsonDTOFormats {

  def create: Action[CreateStudentDTO] = Action(validateJson[CreateStudentDTO]) { implicit request: Request[CreateStudentDTO] =>
    Ok(Json.toJson(Try(studentService.create(request.body)).get))
  }

  def all = Action {
    Ok(Json.toJson(Try(studentService.all).get))
  }

}