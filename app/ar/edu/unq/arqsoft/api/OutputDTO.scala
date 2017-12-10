package ar.edu.unq.arqsoft.api

import ar.edu.unq.arqsoft.api.OutputAlias._
import org.joda.time.DateTime

trait OutputDTO

object OutputAlias {
  type SubjectOfferDTO = Iterable[OfferOptionDTO]
  type SubjectShortName = String
  type CareerOfferDTO = Map[SubjectShortName, SubjectOfferDTO]
  type ResultsDTO = Map[SubjectShortName, OfferOptionDTO]
}

abstract class OfferOptionDTO(key: String, val isCourse: Boolean) extends OutputDTO


case class StudentDTO(fileNumber: Int, email: String, name: String, surname: String, careers: Iterable[PartialCareerDTO], pollResults: Iterable[PartialPollResultDTO]) extends OutputDTO

case class PartialStudentDTO(fileNumber: Int, email: String, name: String, surname: String) extends OutputDTO

case class CareerDTO(shortName: String, longName: String, subjects: Iterable[SubjectDTO], polls: Iterable[PartialPollDTO]) extends OutputDTO

case class PartialCareerDTO(shortName: String, longName: String) extends OutputDTO

case class SubjectDTO(shortName: String, longName: String) extends OutputDTO

case class CourseDTO(key: String, schedules: Iterable[ScheduleDTO]) extends OfferOptionDTO(key, true)

case class ScheduleDTO(day: Int, fromHour: Int, fromMinutes: Int, toHour: Int, toMinutes: Int) extends OutputDTO

case class PollDTO(key: String, isOpen: Boolean, carrer: PartialCareerDTO, offer: CareerOfferDTO) extends OutputDTO

case class PartialPollDTO(key: String, isOpen: Boolean, career: PartialCareerDTO) extends OutputDTO

case class NonCourseOptionDTO(key: String) extends OfferOptionDTO(key, false)

case class PollResultDTO(poll: PartialPollDTO, student: PartialStudentDTO, fillDate: DateTime, results: ResultsDTO) extends OutputDTO

case class PartialPollResultDTO(poll: PartialPollDTO, student: PartialStudentDTO, fillDate: DateTime) extends OutputDTO
