package ar.edu.unq.arqsoft.mappings.json

import ar.edu.unq.arqsoft.api._
import play.api.libs.json._

trait PlayJsonDTOFormats
  extends PlayOutputDTOFormats
    with PlayInputDTOFormats

trait PlayOutputDTOFormats {
  implicit val dateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val partialStudentDTOWrites = Json.writes[PartialStudentDTO]
  implicit val partialCareerDTOWrites = Json.writes[PartialCareerDTO]
  implicit val subjectDTOWrites = Json.writes[SubjectDTO]
  implicit val scheduleDTOWrites = Json.writes[ScheduleDTO]
  implicit val courseDTOWrites = Json.writes[CourseDTO]
  implicit val nonCourseOptionDTOWrites = Json.writes[NonCourseOptionDTO]
  implicit val offerOptionDTOWrites = new Writes[OfferOptionDTO] {
    override def writes(o: OfferOptionDTO): JsValue = o match {
      case course: CourseDTO => courseDTOWrites.writes(course)
      case nonCourse: NonCourseOptionDTO => nonCourseOptionDTOWrites.writes(nonCourse)
    }

  }
  implicit val careerOfferDTOWrites = new Writes[Map[String, OfferOptionDTO]] {
    override def writes(map: Map[String, OfferOptionDTO]): JsValue =
      Json.obj(map.map { case (s, o) =>
        s -> Json.toJsFieldJsValueWrapper(o)
      }.toSeq: _*)
  }
  implicit val resultsDTOWrites = new Writes[Map[String, List[OfferOptionDTO]]] {
    override def writes(map: Map[String, List[OfferOptionDTO]]): JsValue =
      Json.obj(map.map { case (s, o) =>
        s -> Json.toJsFieldJsValueWrapper(o)
      }.toSeq: _*)
  }
  implicit val pollDTOWrites = Json.writes[PollDTO]
  implicit val partialPollDTOWrites = Json.writes[PartialPollDTO]
  implicit val pollResultDTOWrites = Json.writes[PollResultDTO]
  implicit val partialPollResultDTOWrites = Json.writes[PartialPollResultDTO]
  implicit val careerDTOWrites = Json.writes[CareerDTO]
  implicit val studentDTOWrites = Json.writes[StudentDTO]
}

trait PlayInputDTOFormats {
  implicit val createScheduleDTOReads = Json.reads[CreateScheduleDTO]
  implicit val createCourseDTOReads = Json.reads[CreateCourseDTO]
  implicit val createNonCourseDTOReads = Json.reads[CreateNonCourseDTO]
  implicit val createOfferOptionDTOReads = new Reads[CreateOfferOptionDTO] {
    override def reads(json: JsValue): JsResult[CreateOfferOptionDTO] =
      if ((json \ "schedule").isDefined) {
        createCourseDTOReads.reads(json)
      } else {
        createNonCourseDTOReads.reads(json)
      }
  }
  implicit val createPollDTOReads = Json.reads[CreatePollDTO]
  implicit val createSubjectDTOReads = Json.reads[CreateSubjectDTO]
  implicit val createCareerDTOReads = Json.reads[CreateCareerDTO]
  implicit val createStudentDTOReads = Json.reads[CreateStudentDTO]
  implicit val createStudentCareerDTO = Json.reads[CreateStudentCareerDTO]
}