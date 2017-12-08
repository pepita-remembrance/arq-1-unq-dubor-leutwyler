package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api._
import ar.edu.unq.arqsoft.model._
import com.google.inject.Singleton

@Singleton
class PollService extends Service {

  def create(careerShortName: String, dto: CreatePollDTO): PollDTO = inTransaction {
    val career = CareerDAO.whereShortName(careerShortName).single
    val newPoll = dto.asModel(career)
    PollDAO.save(newPoll)
    dto.offer.foreach { offerMap =>
      val subjects = SubjectDAO.whereCareerAndShortNameIn(career, offerMap.keys).toList
      val nonCourses = createNonCourses(offerMap.values.flatten.collect({ case o: CreateNonCourseDTO => o }))
      val offer = offerMap.flatMap {
        case (subjectShortName, options) =>
          val subject = subjects.find(_.shortName == subjectShortName).get
          createOffer(options, nonCourses)
            .map(option => PollOfferOption(newPoll.id, subject.id, option.id))
      }
      PollOfferOptionDAO.save(offer)
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
    CourseDAO.save(courses.map(_._1), useBulk = false)
    val schedules = courses.flatMap { case (course, dto) =>
      dto.schedule.map(_.asModel(course))
    }
    ScheduleDAO.save(schedules)
    val coursesOffer = courses.map(_._1).map(course => (course, OfferOptionBase(course)))
    OfferDAO.save(coursesOffer.map(_._2), useBulk = false)
    coursesOffer
  }

  protected def createNonCourses(nonCoursesDTO: Iterable[CreateNonCourseDTO]): Iterable[(NonCourseOption, OfferOptionBase)] = inTransaction {
    val existingOffer = NonCourseDAO.whereTextValue(nonCoursesDTO.map(_.textValue)).add(OfferDAO.baseOffer).toList
    val existingTextValues = existingOffer.map(_._1.textValue)
    val toCreate = nonCoursesDTO.filterNot(nonCourse => existingTextValues.contains(nonCourse.textValue)).map(_.asModel)
    NonCourseDAO.save(toCreate, useBulk = false)
    val toCreateOffer = toCreate.map(nonCourse => (nonCourse, OfferOptionBase(nonCourse)))
    OfferDAO.save(toCreateOffer.map(_._2), useBulk = false)
    existingOffer ++ toCreateOffer
  }
}
