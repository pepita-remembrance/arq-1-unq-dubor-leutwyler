package ar.edu.unq.arqsoft.mappings.json

import ar.edu.unq.arqsoft.api._
import play.api.libs.json.{JodaWrites, Json}

trait PlayJsonDTOFormats extends PlayOutputDTOFormats

trait PlayOutputDTOFormats {
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