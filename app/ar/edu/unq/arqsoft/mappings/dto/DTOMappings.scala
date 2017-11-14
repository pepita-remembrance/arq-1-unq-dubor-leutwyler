package ar.edu.unq.arqsoft.mappings.dto

import ar.edu.unq.arqsoft.api._
import ar.edu.unq.arqsoft.database.InscriptionPollSchema._
import ar.edu.unq.arqsoft.database.DSLFlavor._
import ar.edu.unq.arqsoft.model._
import org.squeryl.{KeyedEntity, Query}

trait DTOMappings
  extends OutputDTOMappings
    with InputDTOMappings

trait InputDTOMappings {

  implicit class StudentConverter(dto: CreateStudentDTO) extends ModelConverter0[CreateStudentDTO, Student](dto) {
    override def asModel: Student =
      Student(dto.fileNumber, dto.email, dto.name, dto.surname)
  }

  implicit class CareerConverter(dto: CreateCareerDTO) extends ModelConverter0[CreateCareerDTO, Career](dto) {
    override def asModel: Career =
      Career(dto.shortName, dto.longName)
  }

  implicit class SubjectConverter(dto: CreateSubjectDTO) extends ModelConverter1[CreateSubjectDTO, Subject](dto) {
    override type Extra1 = Career

    override def asModel(extra1: Career): Subject =
      Subject(extra1.id, dto.shortName, dto.longName)
  }

  implicit class PollConverter(dto: CreatePollDTO) extends ModelConverter1[CreatePollDTO, Poll](dto) {
    override type Extra1 = Career

    override def asModel(extra1: Career): Poll =
      Poll(dto.key, extra1.id, isOpen = true)
  }

  implicit class OfferOptionConverter(dto: CreateOfferOptionDTO) extends ModelConverter0[CreateOfferOptionDTO, OfferOption](dto) {
    override def asModel: OfferOption = dto match {
      case dto: CreateCourseDTO => dto.asModel
      case dto: CreateNonCourseDTO => dto.asModel
    }
  }

  implicit class NonCourseConverter(dto: CreateNonCourseDTO) extends ModelConverter0[CreateNonCourseDTO, NonCourseOption](dto) {
    override def asModel: NonCourseOption =
      NonCourseOption(dto.textValue)
  }

  implicit class CourseConverter(dto: CreateCourseDTO) extends ModelConverter0[CreateCourseDTO, Course](dto) {
    override def asModel: Course =
      Course(dto.shortName)
  }

  implicit class ScheduleConverter(dto: CreateScheduleDTO) extends ModelConverter1[CreateScheduleDTO, Schedule](dto) {
    override type Extra1 = Course

    override def asModel(extra1: Course): Schedule =
      Schedule(extra1.id, Day(dto.day), dto.fromHour, dto.fromMinutes, dto.toHour, dto.toMinutes)
  }

}

trait OutputDTOMappings {

  implicit class QueryConverter[A](query: Query[A]) {
    def mapAs[B](implicit fun: A => B): Iterable[B] = query.map(fun)
  }

  implicit class IterableConverter[A](iterable: Iterable[A]) {
    def mapAs[B](implicit fun: A => B): Iterable[B] = iterable.map(fun)
  }

  implicit def queryOfferOptionBaseToDTO(query: Query[PollOfferOption]): OutputAlias.CareerOfferDTO = {
    val subjectWithCourse = join(query, subjects, offers, courses)((poo, s, o, c) =>
      where(o.isCourse === true)
        select(s.shortName, c)
        on(poo.subjectId === s.id, poo.offerId === o.id, o.offerId === c.id)
    ).map({ case (subject, option) => subject -> (option: OfferOptionDTO) })
    val subjectWithNonCourse = join(query, subjects, offers, nonCourses)((poo, s, o, nc) =>
      where(o.isCourse === false)
        select(s.shortName, nc)
        on(poo.subjectId === s.id, poo.offerId === o.id, o.offerId === nc.id)
    ).map({ case (subject, option) => subject -> (option: OfferOptionDTO) })
    (subjectWithCourse ++ subjectWithNonCourse)
      .groupBy({ case (subject, _) => subject })
      .mapValues(_.map(_._2))
  }

  implicit def querySelectedOptionsToDTO(query: Query[PollSelectedOption]): OutputAlias.ResultsDTO = {
    val subjectWithCourse = join(query, subjects, offers, courses)((poo, s, o, c) =>
      where(o.isCourse === true)
        select(s.shortName, c)
        on(poo.subjectId === s.id, poo.offerId === o.id, o.offerId === c.id)
    ).map({ case (subject, option) => subject -> (option: OfferOptionDTO) })
    val subjectWithNonCourse = join(query, subjects, offers, nonCourses)((poo, s, o, nc) =>
      where(o.isCourse === false)
        select(s.shortName, nc)
        on(poo.subjectId === s.id, poo.offerId === o.id, o.offerId === nc.id)
    ).map({ case (subject, option) => subject -> (option: OfferOptionDTO) })
    (subjectWithCourse ++ subjectWithNonCourse).toMap
  }

  implicit def studentToPartialDTO(student: Student): PartialStudentDTO =
    PartialStudentDTO(student.fileNumber, student.email, student.name, student.surname)

  implicit def careerToPartialDTO(career: Career): PartialCareerDTO =
    PartialCareerDTO(career.shortName, career.shortName)

  implicit def pollToPartialDTO(poll: Poll): PartialPollDTO =
    PartialPollDTO(poll.key, poll.isOpen, poll.career.single)

  implicit def resultToPartialDTO(pollResult: PollResult): PartialPollResultDTO =
    PartialPollResultDTO(pollResult.poll.single, pollResult.student.single, pollResult.fillDate)

  implicit def subjectToDTO(subject: Subject): SubjectDTO =
    SubjectDTO(subject.shortName, subject.longName)

  implicit def scheduleToDTO(schedule: Schedule): ScheduleDTO =
    ScheduleDTO(schedule.day, schedule.fromHour, schedule.fromMinutes, schedule.toHour, schedule.toMinutes)

  implicit def courseToDTO(course: Course): CourseDTO =
    CourseDTO(course.shortName, course.schedules.mapAs[ScheduleDTO])

  implicit def nonCourseToDTO(nonCourse: NonCourseOption): NonCourseOptionDTO =
    NonCourseOptionDTO(nonCourse.textValue)

  implicit def offerOptionToDTO(offerOption: OfferOption): OfferOptionDTO = offerOption match {
    case offer: Course => offer
    case offer: NonCourseOption => offer
  }

  implicit def pollOfferOptionToDTO(pollOfferOption: PollOfferOption): OfferOptionDTO =
    pollOfferOption.offer.single.discriminated.single

  implicit def pollToDTO(poll: Poll): PollDTO =
    PollDTO(poll.key, poll.isOpen, poll.career.single, poll.offers)

  implicit def careerToDTO(career: Career): CareerDTO =
    CareerDTO(career.shortName, career.longName, career.subjects.mapAs[SubjectDTO], career.polls.mapAs[PartialPollDTO])

  implicit def pollResultToDTO(pollResult: PollResult): PollResultDTO =
    PollResultDTO(pollResult.poll.single, pollResult.student.single, pollResult.fillDate, pollResult.selectedOptions)

  implicit def studentToDTO(student: Student): StudentDTO =
    StudentDTO(student.fileNumber, student.email, student.name, student.surname, student.careers.mapAs[PartialCareerDTO], student.results.mapAs[PartialPollResultDTO])

}


abstract class ModelConverter0[DTO <: InputDTO, Model <: KeyedEntity[_]](dto: DTO) {
  def asModel: Model
}

abstract class ModelConverter1[DTO <: InputDTO, Model <: KeyedEntity[_]](dto: DTO) {
  type Extra1 <: TableRow

  def asModel(extra1: Extra1): Model
}

abstract class ModelConverter2[DTO <: InputDTO, Model <: KeyedEntity[_]](dto: DTO) {
  type Extra1 <: TableRow
  type Extra2 <: TableRow

  def asModel(extra1: Extra1, extra2: Extra2): Model
}

abstract class ModelConverter3[DTO <: InputDTO, Model <: KeyedEntity[_]](dto: DTO) {
  type Extra1 <: TableRow
  type Extra2 <: TableRow
  type Extra3 <: TableRow

  def asModel(extra1: Extra1, extra2: Extra2, extra3: Extra3): Model
}

