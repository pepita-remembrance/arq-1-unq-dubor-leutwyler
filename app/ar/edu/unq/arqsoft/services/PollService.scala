package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api._
import ar.edu.unq.arqsoft.maybe.Maybe
import ar.edu.unq.arqsoft.model._
import ar.edu.unq.arqsoft.repository._
import com.google.inject.{Inject, Singleton}
import org.joda.time.DateTime

@Singleton
class PollService @Inject()(pollRepository: PollRepository,
                            courseRepository: CourseRepository,
                            nonCourseRepository: NonCourseRepository,
                            offerRepository: OfferRepository,
                            studentRepository: StudentRepository,
                            careerRepository: CareerRepository,
                            adminRepository: AdminRepository,
                            subjectRepository: SubjectRepository,
                            pollOfferOptionRepository: PollOfferOptionRepository,
                            pollSubjectOptionRepository: PollSubjectOptionRepository,
                            scheduleRepository: ScheduleRepository
                           ) extends Service {
  def createDefaultOptions(): Maybe[Unit] = {
    val defaultOptions = NonCourseOption.defaultOptionStrings.map(NonCourseOption(_))
    for {
      _ <- nonCourseRepository.save(defaultOptions, useBulk = false)
      defaultOptionsBase = defaultOptions.map(nonCourse => OfferOptionBase(nonCourse))
      _ <- offerRepository.save(defaultOptionsBase)
    } yield ()
  }

  def allOf(studentFileNumber: Int): Maybe[Iterable[PartialPollDTO]] =
    for {
      student <- studentRepository.byFileNumber(studentFileNumber)
      polls <- pollRepository.getOfStudent(student)
    } yield polls.mapAs[PartialPollDTO]

  def allOf(careerShortName: String): Maybe[Iterable[PartialPollDTO]] =
    for {
      career <- careerRepository.byShortName(careerShortName)
      polls <- pollRepository.getOfCareer(career)
    } yield polls.mapAs[PartialPollDTO]

  def allOfAdmin(adminFileNumber: Int): Maybe[Iterable[PartialPollDTO]] =
    for {
      admin <- adminRepository.byFileNumber(adminFileNumber)
      polls <- pollRepository.getOfAdmin(admin)
    } yield polls.mapAs[PartialPollDTO]

  def byCareerShortNameAndPollKey(careerShortName: String, pollKey: String): Maybe[PollDTO] =
    for {
      career <- careerRepository.byShortName(careerShortName)
      poll <- pollRepository.byKeyOfCareer(pollKey, career)
    } yield poll.as[PollDTO]

  def create(careerShortName: String, dto: CreatePollDTO, createDate: DateTime = DateTime.now): Maybe[PollDTO] =
    for {
      career <- careerRepository.byShortName(careerShortName)
      newPoll = dto.asModel(career, createDate)
      _ <- pollRepository.save(newPoll)
            defaultOptions <- nonCourseRepository.byKey(NonCourseOption.defaultOptionStrings)
      offerMap = dto.offer.getOrElse(Map.empty)
      subjects <- subjectRepository.byShortNameOfCareer(offerMap.keys, career)
      nonCourses <- createNonCourses(offerMap.values.flatten.collect({ case o: CreateNonCourseDTO => o }))
      offersLists <- offerMap.map {
        case (subjectShortName, options) =>
          for {
            subject <- Maybe.fromOption(subjects.find(_.shortName == subjectShortName),
              subjectRepository.notFoundByShortNameOfCareer(subjectShortName, career))
            pollOffer <- createOffer(options, nonCourses)
            pollOfferOptions = pollOffer.map(option => PollOfferOption(newPoll.id, subject.id, option._2.id))
            defaultPollOfferOptions = defaultOptions
              .map(_._2)
              .map(baseOffer => PollOfferOption(newPoll.id, subject.id, baseOffer.id))
          } yield pollOfferOptions ++ defaultPollOfferOptions
      }.flattenMaybes
      _ <- pollOfferOptionRepository.save(offersLists.flatten)
      extraData = subjects.map { subject =>
        val subjectExtraData = dto.extraData.getOrElse(Map.empty).getOrElse(subject.shortName, "")
        PollSubjectOption(newPoll.id, subject.id, subjectExtraData)
      }
      _ <- pollSubjectOptionRepository.save(extraData)
    } yield newPoll.as[PollDTO]

  protected def createOffer(optionsDTO: Iterable[CreateOfferOptionDTO], nonCourses: Iterable[(NonCourseOption, OfferOptionBase)]): Maybe[Iterable[(OfferOption, OfferOptionBase)]] =
    for {
    // Squeryl lacks support for UNION queries so...
      courses <- createCourses(optionsDTO.collect { case o: CreateCourseDTO => o })
      usedNonCourses = optionsDTO.collect({ case o: CreateNonCourseDTO => o.key }).map(value => nonCourses.find(_._1.key == value).get)
    } yield courses ++ usedNonCourses

  protected def createCourses(coursesDTO: Iterable[CreateCourseDTO]): Maybe[Iterable[(Course, OfferOptionBase)]] = {
    val courses = coursesDTO.map(dto => (dto.asModel, dto))
    for {
      _ <- courseRepository.save(courses.map(_._1), useBulk = false)
      schedules = courses.flatMap { case (course, dto) =>
        dto.schedules.map(_.asModel(course))
      }
      _ <- scheduleRepository.save(schedules)
      coursesOffer = courses.map(_._1).map(course => (course, OfferOptionBase(course)))
      _ <- offerRepository.save(coursesOffer.map(_._2), useBulk = false)
    } yield coursesOffer
  }

  protected def createNonCourses(nonCoursesDTO: Iterable[CreateNonCourseDTO]): Maybe[Iterable[(NonCourseOption, OfferOptionBase)]] =
    for {
      existingOffer <- nonCourseRepository.byKey(nonCoursesDTO.map(_.key))
      existingTextValues = existingOffer.map(_._1.key).toList
      toCreate = nonCoursesDTO.filterNot(dto => existingTextValues.contains(dto.key)).map(_.asModel)
      _ <- nonCourseRepository.save(toCreate, useBulk = false)
      toCreateOffer = toCreate.map(nonCourse => (nonCourse, OfferOptionBase(nonCourse)))
      _ <- offerRepository.save(toCreateOffer.map(_._2), useBulk = false)
    } yield existingOffer ++ toCreateOffer
}
