package ar.edu.unq.arqsoft.mappings.dto

import ar.edu.unq.arqsoft.api._
import ar.edu.unq.arqsoft.model._
import ar.edu.unq.arqsoft.database.Database._
import org.squeryl.Query

trait DTOMappings {

  implicit class QueryConverter[A](query: Query[A]) {
    def mapAs[B](implicit fun: A => B): Iterable[B] = query.map(fun)
  }

  implicit def queryOfferOptionBaseToDTO(query: Query[PollOfferOption]): OutputAlias.CareerOfferDTO = {
    val subjectWithCourse = join(query, subjects, offers, courses)((poo, s, o, c) =>
      where(o.isCourse === true)
        select(s.shortName, c)
        on(poo.subjectId === s.id, poo.offerId === o.id, o.offerId === c.id)
    ).map({ case (subject, option) => subject -> (option: OfferOptionDTO) })
    val subjectWithNonCourse = join(query, subjects, offers, nonCourses)((poo, s, o, nc) =>
      where(o.isCourse === true)
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
      where(o.isCourse === true)
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
    ScheduleDTO(schedule.day.id, schedule.fromHour, schedule.fromMinutes, schedule.toHour, schedule.toMinutes)

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

}
