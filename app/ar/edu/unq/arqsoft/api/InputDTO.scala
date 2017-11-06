package ar.edu.unq.arqsoft.api

import ar.edu.unq.arqsoft.api.InputAlias._

trait InputDTO

object InputAlias {
  type SubjectShortName = String
}

case class CreateStudent(fileNumber: Int, email: String, name: String, surname: String) extends InputDTO

case class CreateCareer(shortName: String, longName: String, subjects: Option[List[CreateSubject]]) extends InputDTO

case class CreateSubject(shortName: String, longName: String) extends InputDTO

case class CreatePoll(key: String, offer: Option[Map[SubjectShortName, CreateOfferOption]])

trait CreateOfferOption extends InputDTO

case class CreateNonCourse(textValue: String) extends CreateOfferOption

case class CreateCourse(shortName: String, schedule: List[CreateScheduleDTO]) extends CreateOfferOption

case class CreateScheduleDTO(day: Int, fromHour: Int, fromMinutes: Int, toHour: Int, toMinutes: Int) extends InputDTO