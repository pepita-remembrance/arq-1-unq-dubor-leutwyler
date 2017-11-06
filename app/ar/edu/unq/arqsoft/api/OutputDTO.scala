package ar.edu.unq.arqsoft.api

import ar.edu.unq.arqsoft.api.OutputAlias._
import org.joda.time.DateTime

trait OutputDTO

object OutputAlias {
  type SubjectOfferDTO = List[OfferOptionDTO]
  type SubjectShortName = String
  type CareerOfferDTO = Map[SubjectShortName, SubjectOfferDTO]
  type ResultsDTO = Map[SubjectShortName, OfferOptionDTO]
}

abstract class OfferOptionDTO(textValue: String, val isCourse: Boolean) extends OutputDTO


case class StudentDTO(fileNumber: Int, email: String, name: String, surname: String, careers: List[CareerDTO], pollResults: List[PollResultDTO]) extends OutputDTO

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
