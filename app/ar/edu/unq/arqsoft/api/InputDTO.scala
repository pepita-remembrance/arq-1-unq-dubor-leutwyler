package ar.edu.unq.arqsoft.api

import ar.edu.unq.arqsoft.api.InputAlias._

trait InputDTO

object InputAlias {
  type SubjectShortName = String
}

case class CreateStudentDTO(fileNumber: Int, email: String, name: String, surname: String) extends InputDTO

case class CreateCareerDTO(shortName: String, longName: String, subjects: Option[List[CreateSubjectDTO]]) extends InputDTO

case class CreateSubjectDTO(shortName: String, longName: String) extends InputDTO

case class CreatePollDTO(key: String, offer: Option[Map[SubjectShortName, List[CreateOfferOptionDTO]]]) extends InputDTO

sealed abstract class CreateOfferOptionDTO(val isCourse: Boolean) extends InputDTO

case class CreateNonCourseDTO(key: String) extends CreateOfferOptionDTO(false)

case class CreateCourseDTO(key: String, schedules: List[CreateScheduleDTO]) extends CreateOfferOptionDTO(true)

case class CreateScheduleDTO(day: Int, fromHour: Int, fromMinutes: Int, toHour: Int, toMinutes: Int) extends InputDTO

case class CreateStudentCareerDTO(studentFileNumber: Int, careerShortName: String) extends InputDTO

//case class PollSelectedOption(isCourse:Boolean, key:String)