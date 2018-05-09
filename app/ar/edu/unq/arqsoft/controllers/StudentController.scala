package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.api.CreateStudentDTO
import ar.edu.unq.arqsoft.mappings.json.PlayJsonDTOFormats
import ar.edu.unq.arqsoft.security.{JWTService, Role}
import ar.edu.unq.arqsoft.services.{PollResultService, StudentService}
import com.google.inject.{Inject, Singleton}
import play.api.mvc._

@Singleton
class StudentController @Inject()(cc: ControllerComponents, parse: PlayBodyParsers, jwtService: JWTService,
                                  studentService: StudentService,
                                  pollResultService: PollResultService
                                 )
  extends BasicController(cc, parse, jwtService) with PlayJsonDTOFormats {

  def create = JsonAction.withBody[CreateStudentDTO].requires(Role.Admin) {
    implicit request: Request[CreateStudentDTO] =>
      studentService.create(request.body)
  }

  def all = JsonAction.requires(Role.Admin) {
    studentService.all
  }

  def get(fileNumber: Int) = JsonAction.requires(Role.Student) {
    studentService.byFileNumber(fileNumber)
  }

}
