package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.api.{CreateStudentCareerDTO, CreateStudentDTO}
import ar.edu.unq.arqsoft.mappings.json.PlayJsonDTOFormats
import ar.edu.unq.arqsoft.services.{PollResultService, StudentService}
import com.google.inject.{Inject, Singleton}
import play.api.mvc._

@Singleton
class StudentController @Inject()(cc: ControllerComponents, parse: PlayBodyParsers,
                                  studentService: StudentService,
                                  pollResultService: PollResultService
                                 )
  extends BasicController(cc, parse) with PlayJsonDTOFormats {

  def create = JsonAction withBody[CreateStudentDTO] {
    implicit request: Request[CreateStudentDTO] =>
      studentService.create(request.body)
  }

  def all = JsonAction {
    studentService.all
  }

  def get(fileNumber: Int) = JsonAction {
    studentService.byFileNumber(fileNumber)
  }

  def createStudentCareer = JsonAction withBody[CreateStudentCareerDTO] {
    implicit request: Request[CreateStudentCareerDTO] =>
      studentService.joinCareer(request.body)
  }

}
