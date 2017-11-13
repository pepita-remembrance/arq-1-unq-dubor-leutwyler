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

sealed abstract class CreateOfferOptionDTO extends InputDTO

case class CreateNonCourseDTO(textValue: String) extends CreateOfferOptionDTO

case class CreateCourseDTO(shortName: String, schedule: List[CreateScheduleDTO]) extends CreateOfferOptionDTO

case class CreateScheduleDTO(day: Int, fromHour: Int, fromMinutes: Int, toHour: Int, toMinutes: Int) extends InputDTO