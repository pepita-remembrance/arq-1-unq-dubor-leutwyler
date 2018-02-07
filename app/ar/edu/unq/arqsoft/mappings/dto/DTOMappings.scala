package ar.edu.unq.arqsoft.mappings.dto

import ar.edu.unq.arqsoft.api._
import ar.edu.unq.arqsoft.database.DSLFlavor
import ar.edu.unq.arqsoft.database.DSLFlavor._
import ar.edu.unq.arqsoft.database.InscriptionPollSchema._
import ar.edu.unq.arqsoft.model._
import org.joda.time.DateTime
import org.squeryl.{KeyedEntity, Query}

trait DTOMappings
  extends OutputDTOMappings
    with InputDTOMappings

trait InputDTOMappings {

  implicit class StudentConverter(dto: CreateStudentDTO) extends ModelConverter0[CreateStudentDTO, Student](dto) {
    override def asModel: Student =
      Student(dto.fileNumber, dto.email, dto.name, dto.surname)
  }

  implicit class AdminConverter(dto: CreateAdminDTO) extends ModelConverter0[CreateAdminDTO, Admin](dto) {
    override def asModel: Admin =
      Admin(dto.fileNumber, dto.email, dto.name, dto.surname)
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

  implicit class PollConverter(dto: CreatePollDTO) extends ModelConverter2[CreatePollDTO, Poll](dto) {
    override type Extra1 = Career
    override type Extra2 = DateTime

    override def asModel(extra1: Career, extra2: DateTime): Poll =
      Poll(dto.key, extra1.id, isOpen = true, extra2)
  }

  implicit class NonCourseConverter(dto: CreateNonCourseDTO) extends ModelConverter1[CreateNonCourseDTO, NonCourseOption](dto) {

    override type Extra1 = OfferOptionBase

    override def asModel(extra1: OfferOptionBase): NonCourseOption =
      NonCourseOption(dto.key, extra1.id)
  }

  implicit class CourseConverter(dto: CreateCourseDTO) extends ModelConverter1[CreateCourseDTO, Course](dto) {
    override type Extra1 = OfferOptionBase

    override def asModel(extra1: OfferOptionBase): Course =
      Course(dto.key, dto.quota, extra1.id)

    def asModel: Course =
      Course(dto.key, dto.quota, 0)

  }

  implicit class ScheduleConverter(dto: CreateScheduleDTO) extends ModelConverter1[CreateScheduleDTO, Schedule](dto) {
    override type Extra1 = Course

    override def asModel(extra1: Course): Schedule =
      Schedule(extra1.id, Day(dto.day), dto.fromHour, dto.fromMinutes, dto.toHour, dto.toMinutes)
  }

}

trait OutputDTOMappings {

  import MappingUtils._

  implicit def queryPollSubjectOptionToDTO(query: Query[PollSubjectOption]): OutputAlias.ExtraDataDTO = inTransaction {
    join(query, subjects)((ps, s) =>
      select(s.shortName, ps.extraData)
        on (ps.subjectId === s.id)
    ).toMap
  }

  implicit def queryOfferOptionBaseToDTO(query: Query[PollOfferOption]): OutputAlias.CareerOfferDTO = inTransaction {
    val subjectWithCourse = join(query, subjects, courses)((poo, s, c) =>
        select(s.shortName, c)
        on(poo.subjectId === s.id, poo.offerId === c.offerId)
    ).map({ case (subject, option) => subject -> (option: OfferOptionDTO) })
    val subjectWithNonCourse = join(query, subjects, nonCourses)((poo, s, nc) =>
        select(s.shortName, nc)
        on(poo.subjectId === s.id, poo.offerId === nc.offerId)
    ).map({ case (subject, option) => subject -> (option: OfferOptionDTO) })
    (subjectWithCourse ++ subjectWithNonCourse)
      .groupBy({ case (subject, _) => subject })
      .mapValues(_.map(_._2))
  }

  implicit def querySelectedOptionsToDTO(query: Query[PollSelectedOption]): OutputAlias.ResultsDTO = inTransaction {
    val subjectWithCourse = join(query, subjects, courses)((poo, s, c) =>
        select(s.shortName, c)
        on(poo.subjectId === s.id, poo.offerId === c.offerId)
    ).map({ case (subject, option) => subject -> (option: OfferOptionDTO) })
    val subjectWithNonCourse = join(query, subjects, nonCourses)((poo, s, nc) =>
        select(s.shortName, nc)
        on(poo.subjectId === s.id, poo.offerId === nc.offerId)
    ).map({ case (subject, option) => subject -> (option: OfferOptionDTO) })
    (subjectWithCourse ++ subjectWithNonCourse).toMap
  }

  implicit def studentToPartialDTO(student: Student): PartialStudentDTO = inTransaction {
    PartialStudentDTO(student.fileNumber, student.email, student.name, student.surname)
  }

  implicit def adminToPartialDTO(admin: Admin): PartialAdminDTO = inTransaction {
    PartialAdminDTO(admin.fileNumber, admin.email, admin.name, admin.surname)
  }

  implicit def careerToPartialDTO(career: Career): PartialCareerDTO = inTransaction {
    PartialCareerDTO(career.shortName, career.longName)
  }

  implicit def careerToPartialForAdminDTO(career: Career): PartialCareerForAdminDTO = inTransaction {
    PartialCareerForAdminDTO(career.shortName, career.longName, career.students.computeCount)
  }

  implicit def pollToPartialDTO(poll: Poll): PartialPollDTO = inTransaction {
    PartialPollDTO(poll.key, poll.isOpen, poll.career.single)
  }

  implicit def pollToPartialPollForAdminDTO(poll: Poll): PartialPollForAdminDTO = inTransaction {
    PartialPollForAdminDTO(poll.key, poll.isOpen, poll.career.single, poll.results.computeCount)
  }

  implicit def resultToPartialDTO(pollResult: PollResult): PartialPollResultDTO = inTransaction {
    PartialPollResultDTO(pollResult.poll.single, pollResult.fillDate)
  }

  implicit def subjectToDTO(subject: Subject): SubjectDTO = inTransaction {
    SubjectDTO(subject.shortName, subject.longName)
  }

  implicit def scheduleToDTO(schedule: Schedule): ScheduleDTO = inTransaction {
    ScheduleDTO(schedule.day, schedule.fromHour, schedule.fromMinutes, schedule.toHour, schedule.toMinutes)
  }

  implicit def courseToDTO(course: Course): CourseDTO = inTransaction {
    CourseDTO(course.key, course.quota, course.schedules.mapAs[ScheduleDTO])
  }

  implicit def nonCourseToDTO(nonCourse: NonCourseOption): NonCourseOptionDTO = inTransaction {
    NonCourseOptionDTO(nonCourse.key)
  }

  implicit def offerOptionToDTO(offerOption: OfferOption): OfferOptionDTO = inTransaction {
    offerOption match {
      case offer: Course => offer
      case offer: NonCourseOption => offer
    }
  }

  implicit def pollOfferOptionToDTO(pollOfferOption: PollOfferOption): OfferOptionDTO = inTransaction {
    pollOfferOption.offer.single.discriminated
  }

  implicit def pollToDTO(poll: Poll): PollDTO = inTransaction {
    PollDTO(poll.key, poll.isOpen, poll.career.single, poll.offers, poll.extraData)
  }

  implicit def careerToDTO(career: Career): CareerDTO = inTransaction {
    CareerDTO(career.shortName, career.longName, career.subjects.mapAs[SubjectDTO], career.polls.mapAs[PartialPollDTO])
  }

  implicit def pollResultToDTO(pollResult: PollResult): PollResultDTO = inTransaction {
    PollResultDTO(pollResult.poll.single, pollResult.student.single, pollResult.fillDate, pollResult.selectedOptions)
  }

  implicit def studentToDTO(student: Student): StudentDTO = inTransaction {
    val studentPolls = join(polls, studentsCareers)((p, sc) =>
      where((sc.studentId === student.id) and (p.createDate gte sc.joinDate))
        select p
        on (sc.careerId === p.careerId)
    )
    StudentDTO(student.fileNumber, student.email, student.name, student.surname,
      student.careers.mapAs[PartialCareerDTO],
      student.results.mapAs[PartialPollResultDTO],
      studentPolls.mapAs[PartialPollDTO]
    )
  }

  implicit def adminToDTO(admin: Admin): AdminDTO = inTransaction {
    AdminDTO(admin.fileNumber, admin.email, admin.name, admin.surname, admin.careers.mapAs[PartialCareerDTO])
  }

  implicit def optionMapToOptionTallyDTO(data: (OfferOption, Iterable[Student])): OptionTallyDTO = inTransaction {
    OptionTallyDTO(data._1, data._2.mapAs[PartialStudentDTO])
  }

  implicit def tallyMapToDTO(data: (Subject, Map[OfferOption, Iterable[Student]])): TallyDTO = inTransaction {
    TallyDTO(data._1, data._2.mapAs[OptionTallyDTO])
  }
}


abstract class ModelConverter0[DTO <: InputDTO, Model <: KeyedEntity[_]](dto: DTO) {
  def asModel: Model
}

abstract class ModelConverter1[DTO <: InputDTO, Model <: KeyedEntity[_]](dto: DTO) {
  type Extra1

  def asModel(extra1: Extra1): Model
}

abstract class ModelConverter2[DTO <: InputDTO, Model <: KeyedEntity[_]](dto: DTO) {
  type Extra1
  type Extra2

  def asModel(extra1: Extra1, extra2: Extra2): Model
}

object MappingUtils {

  implicit class QueryConverter[A](query: Query[A]) {
    def mapAs[B](implicit fun: A => B): Iterable[B] = query.map(fun)

    def computeCount: Long = from(query)(q => compute(DSLFlavor.count)).single.measures
  }

  implicit class IterableConverter[A](iterable: Iterable[A]) {
    def mapAs[B](implicit fun: A => B): Iterable[B] = iterable.map(fun)
  }

}