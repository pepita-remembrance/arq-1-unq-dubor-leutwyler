package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.DAOs._
import ar.edu.unq.arqsoft.api._
import ar.edu.unq.arqsoft.model._
import com.google.inject.{Inject, Singleton}

@Singleton
class PollService @Inject()(pollDAO: PollDAO,
                            careerDAO: CareerDAO,
                            subjectDAO: SubjectDAO,
                            courseDAO: CourseDAO,
                            nonCourseDAO: NonCourseDAO,
                            offerDAO: OfferDAO,
                            pollOfferOptionDAO: PollOfferOptionDAO,
                            scheduleDAO: ScheduleDAO
                           ) extends Service {

  def create(careerShortName: String, dto: CreatePollDTO): PollDTO = inTransaction {
    val career = careerDAO.whereShortName(careerShortName).single
    val newPoll = dto.asModel(career)
    pollDAO.save(newPoll)
    dto.offer.foreach { offerMap =>
      val subjects = subjectDAO.whereCareerAndShortNameIn(career, offerMap.keys).toList
      val nonCourses = createNonCourses(offerMap.values.flatten.collect({ case o: CreateNonCourseDTO => o }))
      val offer = offerMap.flatMap {
        case (subjectShortName, options) =>
          val subject = subjects.find(_.shortName == subjectShortName).get
          createOffer(options, nonCourses)
            .map(option => PollOfferOption(newPoll.id, subject.id, option.id))
      }
      pollOfferOptionDAO.save(offer)
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
    courseDAO.save(courses.map(_._1), useBulk = false)
    val schedules = courses.flatMap { case (course, dto) =>
      dto.schedule.map(_.asModel(course))
    }
    scheduleDAO.save(schedules)
    val coursesOffer = courses.map(_._1).map(course => (course, OfferOptionBase(course)))
    offerDAO.save(coursesOffer.map(_._2), useBulk = false)
    coursesOffer
  }

  protected def createNonCourses(nonCoursesDTO: Iterable[CreateNonCourseDTO]): Iterable[(NonCourseOption, OfferOptionBase)] = inTransaction {
    val existingOffer = offerDAO.nonCourses.toList
    val existingTextValues = existingOffer.map(_._1.textValue)
    val toCreate = nonCoursesDTO.filterNot(nonCourse => existingTextValues.contains(nonCourse.textValue)).map(_.asModel)
    nonCourseDAO.save(toCreate, useBulk = false)
    val toCreateOffer = toCreate.map(nonCourse => (nonCourse, OfferOptionBase(nonCourse)))
    offerDAO.save(toCreateOffer.map(_._2), useBulk = false)
    existingOffer ++ toCreateOffer
  }
}
