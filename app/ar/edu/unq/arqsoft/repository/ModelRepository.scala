package ar.edu.unq.arqsoft.repository

import javax.inject.Inject

import ar.edu.unq.arqsoft.DAOs._
import ar.edu.unq.arqsoft.maybe.{EntityNotFound, Maybe}
import ar.edu.unq.arqsoft.model.TableRow.KeyType
import ar.edu.unq.arqsoft.model._
import com.google.inject.Singleton

class ModelRepository[T <: TableRow](dao: ModelDAO[T])
  extends Repository[T, KeyType](dao)

@Singleton
class StudentRepository @Inject()(dao: StudentDAO)
  extends ModelRepository[Student](dao) {
  def notFoundByFileNumber(fileNumber: Int): EntityNotFound =
    notFoundBy("file number", fileNumber)

  def byFileNumber(fileNumber: Int): Maybe[Student] =
    singleResult(dao.byFileNumber(fileNumber), notFoundByFileNumber(fileNumber))
}

@Singleton
class AdminRepository @Inject()(dao: AdminDAO)
  extends ModelRepository[Admin](dao) {
  def notFoundByFileNumber(fileNumber: Int): EntityNotFound =
    notFoundBy("file number", fileNumber)

  def byFileNumber(fileNumber: Int): Maybe[Admin] =
    singleResult(dao.byFileNumber(fileNumber), notFoundByFileNumber(fileNumber))
}

@Singleton
class CareerRepository @Inject()(dao: CareerDAO)
  extends ModelRepository[Career](dao) {
  def notFoundByShortName(shortName: String): EntityNotFound =
    notFoundBy("short name", shortName)

  def byShortName(shortName: String): Maybe[Career] =
    singleResult(dao.byShortName(shortName), notFoundByShortName(shortName))

  def getOfAdmin(admin: Admin): Maybe[Iterable[Career]] = inTransaction {
    dao.getOfAdmin(admin.id).toList
  }
}

@Singleton
class SubjectRepository @Inject()(dao: SubjectDAO)
  extends ModelRepository[Subject](dao) {
  def notFoundByShortNameOfCareer(shortName: String, career: Career): EntityNotFound =
    notFoundBy("(career, short name)", (career.shortName, shortName))

  def byShortNameOfCareer(shortNames: Iterable[String], career: Career): Maybe[Iterable[Subject]] = inTransaction {
    dao.byShortNameOfCareer(shortNames, career.id).toList
  }

  def getOfPoll(poll: Poll): Maybe[Iterable[Subject]] = inTransaction {
    dao.getOfPoll(poll.id).toList
  }
}

@Singleton
class OfferRepository @Inject()(dao: OfferDAO)
  extends ModelRepository[OfferOptionBase](dao)

@Singleton
class CourseRepository @Inject()(dao: CourseDAO)
  extends ModelRepository[Course](dao) {
  def getOfPollBySubjectName(poll: Poll, subjectShortNames: Iterable[String]): Maybe[Iterable[(Subject, Course, OfferOptionBase)]] = inTransaction {
    dao.getOfPollBySubjectName(poll.id, subjectShortNames).toList
  }

  def tallyByPoll(poll: Poll): Maybe[Iterable[(Subject, Course, Student)]] = inTransaction {
    dao.tallyByPoll(poll.id).toList
  }
}

@Singleton
class NonCourseRepository @Inject()(dao: NonCourseDAO)
  extends ModelRepository[NonCourseOption](dao) {
  def notFoundByKey(key: String): EntityNotFound =
    notFoundBy("key", key)

  def byKey(keys: Iterable[String]): Maybe[Iterable[(NonCourseOption, OfferOptionBase)]] = inTransaction {
    dao.byKey(keys).toList
  }

  def byKey(key: String): Maybe[(NonCourseOption, OfferOptionBase)] =
    singleResult(dao.byKey(key), notFoundByKey(key))

  def getOfSubjectByPoll(subjectShortNames: Iterable[String], poll: Poll): Maybe[Iterable[(Subject, NonCourseOption, OfferOptionBase)]] = inTransaction {
    dao.getOfSubjectByPoll(subjectShortNames, poll.id).toList
  }

  def tallyByPoll(poll: Poll): Maybe[Iterable[(Subject, NonCourseOption, Student)]] = inTransaction {
    dao.tallyByPoll(poll.id).toList
  }
}

@Singleton
class ScheduleRepository @Inject()(dao: ScheduleDAO)
  extends ModelRepository[Schedule](dao)

@Singleton
class PollRepository @Inject()(dao: PollDAO)
  extends ModelRepository[Poll](dao) {
  def notFoundByKeyOfCareer(career: Career, pollKey: String): EntityNotFound =
    notFoundBy("(career, key)", (career.shortName, pollKey))

  def byKeyOfCareer(pollKey: String, career: Career): Maybe[Poll] =
    singleResult(dao.byKeyOfCareer(pollKey, career.id), notFoundByKeyOfCareer(career, pollKey))

  def getOfCareer(career: Career): Maybe[Iterable[Poll]] = inTransaction {
    dao.getOfCareer(career.id).toList
  }

  def getOfStudent(student: Student): Maybe[Iterable[Poll]] = inTransaction {
    dao.getOfStudent(student.id).toList
  }

  def getOfAdmin(admin: Admin): Maybe[Iterable[Poll]] = inTransaction {
    dao.getOfAdmin(admin.id).toList
  }
}

@Singleton
class PollResultRepository @Inject()(dao: PollResultDAO)
  extends ModelRepository[PollResult](dao) {
  def notFoundByStudentAndPoll(student: Student, career: Career, pollKey: String): EntityNotFound =
    notFoundBy("(student, poll)", (student.email, (career.shortName, pollKey)))

  def byStudentAndPoll(student: Student, poll: Poll, career: Career): Maybe[PollResult] =
    singleResult(dao.byStudentAndPoll(student.id, poll.id), notFoundByStudentAndPoll(student, career, poll.key))
}

@Singleton
class PollSubjectOptionRepository @Inject()(dao: PollSubjectOptionDAO)
  extends ModelRepository[PollSubjectOption](dao)

@Singleton
class PollOfferOptionRepository @Inject()(dao: PollOfferOptionDAO)
  extends ModelRepository[PollOfferOption](dao)

@Singleton
class PollSelectedOptionRepository @Inject()(dao: PollSelectedOptionDAO)
  extends ModelRepository[PollSelectedOption](dao) {
  def getOfAlreadyPassedSubjects(pollResult: PollResult): Maybe[Iterable[PollSelectedOption]] = inTransaction {
    dao.getByPollAndOfPassedSubjectsOfStudent(pollResult.id, pollResult.studentId).toList
  }

  def getOfPollResultBySubjectName(pollResult: PollResult, subjectShortNames: Iterable[String]): Maybe[Iterable[(Subject, PollSelectedOption)]] = inTransaction {
    dao.getOfPollResult(pollResult.id, subjectShortNames).toList
  }
}

@Singleton
class StudentCareerRepository @Inject()(dao: StudentCareerDAO)
  extends ModelRepository[StudentCareer](dao)

@Singleton
class AdminCareerRepository @Inject()(dao: AdminCareerDAO)
  extends ModelRepository[AdminCareer](dao)










