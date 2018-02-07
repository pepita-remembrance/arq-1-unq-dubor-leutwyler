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
    val defaultBases = NonCourseOption.defaultOptionStrings.map((_, OfferOptionBase.forNonCourse))
    for {
      _ <- offerRepository.save(defaultBases.map(_._2), useBulk = false)
      defaultsNonCourses = defaultBases.map { case (key, base) => NonCourseOption(key, base.id) }
      _ <- nonCourseRepository.save(defaultsNonCourses, useBulk = false)
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
            pollOfferOptions = pollOffer.map(option => PollOfferOption(newPoll.id, subject.id, option.offerId))
            defaultPollOfferOptions = defaultOptions.map(option => PollOfferOption(newPoll.id, subject.id, option.offerId))
          } yield pollOfferOptions ++ defaultPollOfferOptions
      }.flattenMaybes
      _ <- pollOfferOptionRepository.save(offersLists.flatten)
      extraData = subjects.map { subject =>
        val subjectExtraData = dto.extraData.getOrElse(Map.empty).getOrElse(subject.shortName, "")
        PollSubjectOption(newPoll.id, subject.id, subjectExtraData)
      }
      _ <- pollSubjectOptionRepository.save(extraData)
    } yield newPoll.as[PollDTO]

  protected def createOffer(optionsDTO: Iterable[CreateOfferOptionDTO], nonCourses: Iterable[NonCourseOption]): Maybe[Iterable[OfferOption]] =
    for {
    // Squeryl lacks support for UNION queries so...
      courses <- createCourses(optionsDTO.collect { case o: CreateCourseDTO => o })
      usedNonCourses = optionsDTO.collect({ case o: CreateNonCourseDTO => o.key }).map(value => nonCourses.find(_.key == value).get)
    } yield courses ++ usedNonCourses

  protected def createCourses(coursesDTO: Iterable[CreateCourseDTO]): Maybe[Iterable[Course]] = {
    val toCreate = coursesDTO.map((_, OfferOptionBase.forCourse))
    for {
      _ <- offerRepository.save(toCreate.map(_._2), useBulk = false)
      coursesOffer = toCreate.map { case (dto, base) => (dto, dto.asModel(base)) }
      _ <- courseRepository.save(coursesOffer.map(_._2), useBulk = false)
      schedules = coursesOffer.flatMap { case (dto, course) =>
        dto.schedules.map(_.asModel(course))
      }
      _ <- scheduleRepository.save(schedules)
    } yield coursesOffer.map(_._2)
  }

  protected def createNonCourses(nonCoursesDTO: Iterable[CreateNonCourseDTO]): Maybe[Iterable[NonCourseOption]] =
    for {
      existingOffer <- nonCourseRepository.byKey(nonCoursesDTO.map(_.key))
      existingTextValues = existingOffer.map(_.key).toList
      toCreate = nonCoursesDTO.filterNot(dto => existingTextValues.contains(dto.key)).map((_, OfferOptionBase.forNonCourse))
      _ <- offerRepository.save(toCreate.map(_._2), useBulk = false)
      toCreateOffer = toCreate.map { case (dto, base) => dto.asModel(base) }
      _ <- nonCourseRepository.save(toCreateOffer, useBulk = false)
    } yield existingOffer ++ toCreateOffer
}
