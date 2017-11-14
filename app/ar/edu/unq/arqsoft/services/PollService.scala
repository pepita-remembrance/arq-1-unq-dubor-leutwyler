package ar.edu.unq.arqsoft.services

import javax.inject.{Inject, Singleton}

import ar.edu.unq.arqsoft.DAOs._
import ar.edu.unq.arqsoft.api._
import ar.edu.unq.arqsoft.model._
import ar.edu.unq.arqsoft.database.DSLFlavor._

@Singleton
class PollService @Inject()(pollDAO: PollDAO, careerDAO: CareerDAO, subjectDAO: SubjectDAO, courseDAO: CourseDAO, nonCourseDAO: NonCourseDAO, offerDAO: OfferDAO, pollOfferOptionDAO: PollOfferOptionDAO, scheduleDAO: ScheduleDAO)
  extends Service[Poll] {

  def create(careerShortName: String, dto: CreatePollDTO): PollDTO = inTransaction {
    val career = careerDAO.search(_.shortName === careerShortName).single
    val newPoll = dto.asModel(career)
    pollDAO.save(newPoll)
    dto.offer.foreach { offerMap =>
      val subjects = subjectDAO.search(_.careerId === career.id, _.shortName in offerMap.keys)
      val nonCourses = createNonCourses(offerMap.values.flatten.collect({ case o: CreateNonCourseDTO => o }))
      offerMap.foreach {
        case (subjectShortName, options) =>
          val subject = subjects.find(_.shortName == subjectShortName).get
          val offer = createOffer(options, nonCourses)
            .map(option => PollOfferOption(newPoll.id, subject.id, option.id))
          pollOfferOptionDAO.save(offer)
      }
    }
    newPoll
  }

  protected def createOffer(optionsDTO: Iterable[CreateOfferOptionDTO], nonCoursesDTO: Iterable[(NonCourseOption, OfferOptionBase)]): Iterable[OfferOptionBase] = inTransaction {
    val courses = createCourses(optionsDTO.collect { case o: CreateCourseDTO => o }).map(_._2)
    val usedNonCourses = optionsDTO.collect({ case o: CreateNonCourseDTO => o.textValue }).map(value => nonCoursesDTO.find(_._1.textValue == value).get._2)
    courses ++ usedNonCourses
  }

  protected def createCourses(coursesDTO: Iterable[CreateCourseDTO]): Iterable[(Course, OfferOptionBase)] = inTransaction {
    val courses = coursesDTO.map(dto => (dto.asModel, dto))
    courseDAO.save(courses.map(_._1))
    val schedules = courses.flatMap { case (course, dto) =>
      dto.schedule.map(_.asModel(course))
    }
    scheduleDAO.save(schedules)
    val coursesOffer = courses.map(_._1).map(course => (course, OfferOptionBase(course)))
    offerDAO.save(coursesOffer.map(_._2))
    coursesOffer
  }

  protected def createNonCourses(nonCoursesDTO: Iterable[CreateNonCourseDTO]): Iterable[(NonCourseOption, OfferOptionBase)] = inTransaction {
    val existingOffer = join(
      nonCourseDAO.search(_.textValue in nonCoursesDTO.map(_.textValue)),
      offerDAO.search(_.isCourse === true))((nc, o) =>
      select(nc, o)
        on (nc.id === o.offerId)).toList
    val existingTextValues = existingOffer.map(_._1.textValue)
    val toCreate = nonCoursesDTO.filterNot(nonCourse => existingTextValues.contains(nonCourse.textValue)).map(_.asModel)
    nonCourseDAO.save(toCreate)
    val toCreateOffer = toCreate.map(nonCourse => (nonCourse, OfferOptionBase(nonCourse)))
    offerDAO.save(toCreateOffer.map(_._2))
    existingOffer ++ toCreateOffer
  }
}
