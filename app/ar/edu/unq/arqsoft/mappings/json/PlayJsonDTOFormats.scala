package ar.edu.unq.arqsoft.mappings.json

import ar.edu.unq.arqsoft.api._
import play.api.libs.json.{JodaWrites, Json}

trait PlayJsonDTOFormats
  extends PlayOutputDTOFormats
    with PlayInputDTOFormats

trait PlayOutputDTOFormats {
  implicit val dateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val studentDTOWrites = Json.writes[StudentDTO]
  implicit val partialStudentDTOWrites = Json.writes[PartialStudentDTO]
  implicit val careerDTOWrites = Json.writes[CareerDTO]
  implicit val partialCareerDTOWrites = Json.writes[PartialCareerDTO]
  implicit val subjectDTOWrites = Json.writes[SubjectDTO]
  implicit val courseDTOWrites = Json.writes[CourseDTO]
  implicit val scheduleDTOWrites = Json.writes[ScheduleDTO]
  implicit val pollDTOWrites = Json.writes[PollDTO]
  implicit val partialPollDTOWrites = Json.writes[PartialPollDTO]
  implicit val nonCourseOptionDTOWrites = Json.writes[NonCourseOptionDTO]
  implicit val pollResultDTOWrites = Json.writes[PollResultDTO]
  implicit val partialPollResultDTOWrites = Json.writes[PartialPollResultDTO]
}

trait PlayInputDTOFormats {
  implicit val createStudentDTOReads = Json.reads[CreateStudentDTO]
  implicit val createCareerDTOReads = Json.reads[CreateCareerDTO]
  implicit val createSubjectDTOReads = Json.reads[CreateSubjectDTO]
  implicit val createPollDTOReads = Json.reads[CreatePollDTO]
  implicit val createNonCourseDTOReads = Json.reads[CreateNonCourseDTO]
  implicit val createCourseDTOReads = Json.reads[CreateCourseDTO]
  implicit val createScheduleDTOReads = Json.reads[CreateScheduleDTO]
}