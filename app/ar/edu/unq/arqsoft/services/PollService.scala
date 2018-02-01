package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api._
import ar.edu.unq.arqsoft.maybe.{Maybe, Something}
import ar.edu.unq.arqsoft.model._
import com.google.inject.Singleton
import org.joda.time.DateTime

@Singleton
class PollService extends Service {
  def createDefaultOptions(): Maybe[Unit] = inTransaction {
    val defaultOptions = NonCourseOption.defaultOptionStrings.map(NonCourseOption(_))
    NonCourseDAO.save(defaultOptions, useBulk = false)
    val defaultOptionsBase = defaultOptions.map(nonCourse => OfferOptionBase(nonCourse))
    OfferDAO.save(defaultOptionsBase)
    Something(())
  }

  def allOf(studentFileNumber: Int): Maybe[Iterable[PartialPollDTO]] = inTransaction {
    PollDAO.pollsOfStudent(studentFileNumber).mapAs[PartialPollDTO]
  }

  def allOf(careerShortName: String): Maybe[Iterable[PartialPollDTO]] = inTransaction {
    PollDAO.pollsOfCareer(careerShortName).mapAs[PartialPollDTO]
  }

  def allOfAdmin(adminFileNumber: Int): Maybe[Iterable[PartialPollDTO]] = inTransaction {
    PollDAO.pollsOfAdmin(adminFileNumber).mapAs[PartialPollDTO]
  }

  def byCareerShortNameAndPollKey(careerShortName: String, pollKey: String): Maybe[PollDTO] = inTransaction {
    Something(PollDAO.pollByCareerAndKey(careerShortName, pollKey).single)
  }

  def create(careerShortName: String, dto: CreatePollDTO, createDate: DateTime = DateTime.now): Maybe[PollDTO] = inTransaction {
    val careerQuery = CareerDAO.byShortName(careerShortName)
    val newPoll = dto.asModel(careerQuery.single, createDate)
    PollDAO.save(newPoll)
    dto.offer.foreach { offerMap =>
      val defaultOptions = OfferDAO.baseOfferOfNonCourse(NonCourseOption.defaultOptionStrings).toList
      val subjects = SubjectDAO.subjectsOfCareerWithName(careerShortName, offerMap.keys).toList
      val nonCourses = createNonCourses(offerMap.values.flatten.collect({ case o: CreateNonCourseDTO => o })).get
      val offer = offerMap.flatMap {
        case (subjectShortName, options) =>
          val subject = subjects.find(_.shortName == subjectShortName).get
          val pollOfferOptions = createOffer(options, nonCourses).get
            .map(option => PollOfferOption(newPoll.id, subject.id, option.id))
          val defaultPollOfferOptions = defaultOptions.map(option => PollOfferOption(newPoll.id, subject.id, option.id))
          pollOfferOptions ++ defaultPollOfferOptions
      }
      PollOfferOptionDAO.save(offer)
      val extraData = subjects.map { subject =>
        val subjectExtraData = dto.extraData.getOrElse(Map.empty[String, String]).getOrElse(subject.shortName, "")
        PollSubjectOption(newPoll.id, subject.id, subjectExtraData)
      }
      PollSubjectOptionDAO.save(extraData)
    }
    Something(newPoll)
  }

  protected def createOffer(optionsDTO: Iterable[CreateOfferOptionDTO], nonCoursesDTO: Iterable[(NonCourseOption, OfferOptionBase)]): Maybe[Iterable[OfferOptionBase]] = inTransaction {
    val courses = createCourses(optionsDTO.collect { case o: CreateCourseDTO => o }).get.map(_._2)
    val usedNonCourses = optionsDTO.collect({ case o: CreateNonCourseDTO => o.key }).map(value => nonCoursesDTO.find(_._1.key == value).get._2)
    Something(courses ++ usedNonCourses)
  }

  protected def createCourses(coursesDTO: Iterable[CreateCourseDTO]): Maybe[Iterable[(Course, OfferOptionBase)]] = inTransaction {
    val courses = coursesDTO.map(dto => (dto.asModel, dto))
    CourseDAO.save(courses.map(_._1), useBulk = false)
    val schedules = courses.flatMap { case (course, dto) =>
      dto.schedules.map(_.asModel(course))
    }
    ScheduleDAO.save(schedules)
    val coursesOffer = courses.map(_._1).map(course => (course, OfferOptionBase(course)))
    OfferDAO.save(coursesOffer.map(_._2), useBulk = false)
    Something(coursesOffer)
  }

  protected def createNonCourses(nonCoursesDTO: Iterable[CreateNonCourseDTO]): Maybe[Iterable[(NonCourseOption, OfferOptionBase)]] = inTransaction {
    val existingOffer = NonCourseDAO.whereTextValueWithBaseOffer(nonCoursesDTO.map(_.key)).toList
    val existingTextValues = existingOffer.map(_._1.key)
    val toCreate = nonCoursesDTO.filterNot(nonCourse => existingTextValues.contains(nonCourse.key)).map(_.asModel)
    NonCourseDAO.save(toCreate, useBulk = false)
    val toCreateOffer = toCreate.map(nonCourse => (nonCourse, OfferOptionBase(nonCourse)))
    OfferDAO.save(toCreateOffer.map(_._2), useBulk = false)
    Something(existingOffer ++ toCreateOffer)
  }
}
