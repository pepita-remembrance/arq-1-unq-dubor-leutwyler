package ar.edu.unq.arqsoft.api

import ar.edu.unq.arqsoft.api.Alias.{CareerOfferDTO, ResultsDTO}
import org.joda.time.DateTime
import play.api.libs.json.{JodaWrites, Json}

trait OutputDTO

object Alias {
  type CareerOfferDTO = Map[SubjectDTO, SubjectOfferDTO]
  type SubjectOfferDTO = List[OfferOptionDTO]
  type ResultsDTO = Map[SubjectDTO, OfferOptionDTO]
}

abstract class OfferOptionDTO(textValue: String, isCourse: Boolean) extends OutputDTO


case class StudentDTO(fileNumber: Int, name: String, surname: String, careers: List[CareerDTO], pollResults: List[PollResultDTO]) extends OutputDTO

case class PartialStudentDTO(fileNumber: Int, name: String, surname: String) extends OutputDTO

case class CareerDTO(shortName: String, longName: String, subjects: List[SubjectDTO], polls: List[PollDTO]) extends OutputDTO

case class PartialCareerDTO(shortName: String, longName: String) extends OutputDTO

case class SubjectDTO(shortName: String, FullName: String) extends OutputDTO

case class CourseDTO(shortName: String, schedules: List[ScheduleDTO]) extends OfferOptionDTO(shortName, true)

case class ScheduleDTO(day: Int, fromHour: Int, fromMinutes: Int, toHour: Int, toMinutes: Int) extends OutputDTO

case class PollDTO(key: String, isOpen: Boolean, carrer: PartialCareerDTO, offer: CareerOfferDTO) extends OutputDTO

case class PartialPollDTO(key: String, isOpen: Boolean, carrer: PartialCareerDTO) extends OutputDTO

case class NonCourseOptionDTO(textValue: String) extends OfferOptionDTO(textValue, false)

case class PollResultDTO(poll: PollDTO, student: PartialStudentDTO, fillDate: DateTime, results: ResultsDTO) extends OutputDTO

case class PartialPollResultDTO(poll: PartialPollDTO, student: PartialStudentDTO, fillDate: DateTime) extends OutputDTO


trait OutputDTOFormats {
  implicit val dateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val studentDTOFormat = Json.writes[StudentDTO]
  implicit val partialStudentDTOFormat = Json.writes[PartialStudentDTO]
  implicit val careerDTOFormat = Json.writes[CareerDTO]
  implicit val partialCareerDTOFormat = Json.writes[PartialCareerDTO]
  implicit val subjectDTOFormat = Json.writes[SubjectDTO]
  implicit val courseDTOFormat = Json.writes[CourseDTO]
  implicit val scheduleDTOFormat = Json.writes[ScheduleDTO]
  implicit val pollDTOFormat = Json.writes[PollDTO]
  implicit val partialPollDTOFormat = Json.writes[PartialPollDTO]
  implicit val nonCourseOptionDTOFormat = Json.writes[NonCourseOptionDTO]
  implicit val pollResultDTOFormat = Json.writes[PollResultDTO]
  implicit val partialPollResultDTOFormat = Json.writes[PartialPollResultDTO]
}