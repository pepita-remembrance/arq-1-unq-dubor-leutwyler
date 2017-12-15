package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api._
import ar.edu.unq.arqsoft.model._
import com.google.inject.Singleton
import org.joda.time.DateTime

@Singleton
class PollService extends Service {
  def createDefaultOptions(): Unit = inTransaction {
    val defaultOptions = NonCourseDAO.defaultOptionStrings.map(NonCourseOption)
    NonCourseDAO.save(defaultOptions, useBulk = false)
    val defaultOptionsBase = defaultOptions.map(nonCourse => OfferOptionBase(nonCourse))
    OfferDAO.save(defaultOptionsBase)
  }

  def allOf(studentFileNumber: Int): Iterable[PartialPollDTO] = inTransaction {
    PollDAO.pollsOfStudent(StudentCareerDAO.whereStudent(StudentDAO.whereFileNumber(studentFileNumber))).mapAs[PartialPollDTO]
  }

  def allOf(careerShortName: String): Iterable[PartialPollDTO] = inTransaction {
    PollDAO.pollsOfCareer(CareerDAO.whereShortName(careerShortName)).mapAs[PartialPollDTO]
  }

  def allOfAdmin(adminFileNumber: Int): Iterable[PartialPollDTO] = inTransaction {
    PollDAO.pollsOfAdmin(AdminCareerDAO.whereAdmin(AdminDAO.whereFileNumber(adminFileNumber))).mapAs[PartialPollDTO]
  }

  def byCareerShortNameAndPollKey(careerShortName: String, pollKey: String): PollDTO = inTransaction {
    PollDAO.pollsOfCareerWithKey(CareerDAO.whereShortName(careerShortName), pollKey).single
  }

  def create(careerShortName: String, dto: CreatePollDTO, createDate: DateTime = DateTime.now): PollDTO = inTransaction {
    val careerQuery = CareerDAO.whereShortName(careerShortName)
    val newPoll = dto.asModel(careerQuery.single, createDate)
    PollDAO.save(newPoll)
    dto.offer.foreach { offerMap =>
      val defaultOptions = OfferDAO.baseOfferOfNonCourse(NonCourseDAO.defaultOptions).toList
      val subjects = SubjectDAO.subjectsOfCareerWithName(careerQuery, offerMap.keys).toList
      val nonCourses = createNonCourses(offerMap.values.flatten.collect({ case o: CreateNonCourseDTO => o }))
      val offer = offerMap.flatMap {
        case (subjectShortName, options) =>
          val subject = subjects.find(_.shortName == subjectShortName).get
          val pollOfferOptions = createOffer(options, nonCourses)
            .map(option => PollOfferOption(newPoll.id, subject.id, option.id))
          val defaultPollOfferOptions = defaultOptions.map(option => PollOfferOption(newPoll.id, subject.id, option.id))
          pollOfferOptions ++ defaultPollOfferOptions
      }
      PollOfferOptionDAO.save(offer)
    }
    newPoll
  }

  protected def createOffer(optionsDTO: Iterable[CreateOfferOptionDTO], nonCoursesDTO: Iterable[(NonCourseOption, OfferOptionBase)]): Iterable[OfferOptionBase] = inTransaction {
    val courses = createCourses(optionsDTO.collect { case o: CreateCourseDTO => o }).map(_._2)
    val usedNonCourses = optionsDTO.collect({ case o: CreateNonCourseDTO => o.key }).map(value => nonCoursesDTO.find(_._1.key == value).get._2)
    courses ++ usedNonCourses
  }

  protected def createCourses(coursesDTO: Iterable[CreateCourseDTO]): Iterable[(Course, OfferOptionBase)] = inTransaction {
    val courses = coursesDTO.map(dto => (dto.asModel, dto))
    CourseDAO.save(courses.map(_._1), useBulk = false)
    val schedules = courses.flatMap { case (course, dto) =>
      dto.schedules.map(_.asModel(course))
    }
    ScheduleDAO.save(schedules)
    val coursesOffer = courses.map(_._1).map(course => (course, OfferOptionBase(course)))
    OfferDAO.save(coursesOffer.map(_._2), useBulk = false)
    coursesOffer
  }

  protected def createNonCourses(nonCoursesDTO: Iterable[CreateNonCourseDTO]): Iterable[(NonCourseOption, OfferOptionBase)] = inTransaction {
    val existingOffer = OfferDAO.addBaseOfferOfNonCourse(NonCourseDAO.whereTextValue(nonCoursesDTO.map(_.key))).toList
    val existingTextValues = existingOffer.map(_._1.key)
    val toCreate = nonCoursesDTO.filterNot(nonCourse => existingTextValues.contains(nonCourse.key)).map(_.asModel)
    NonCourseDAO.save(toCreate, useBulk = false)
    val toCreateOffer = toCreate.map(nonCourse => (nonCourse, OfferOptionBase(nonCourse)))
    OfferDAO.save(toCreateOffer.map(_._2), useBulk = false)
    existingOffer ++ toCreateOffer
  }
}
