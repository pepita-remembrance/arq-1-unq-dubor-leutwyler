package ar.edu.unq.arqsoft.DAOs

import ar.edu.unq.arqsoft.database.DSLFlavor._
import ar.edu.unq.arqsoft.database.InscriptionPollSchema._
import ar.edu.unq.arqsoft.model.TableRow.KeyType
import ar.edu.unq.arqsoft.model._
import com.google.inject.Singleton
import org.squeryl.{CanLookup, KeyedEntityDef, Query, Table}

class ModelDAO[T <: TableRow](table: Table[T])
                             (implicit ked: KeyedEntityDef[T, KeyType], toCanLookup: KeyType => CanLookup)
  extends SquerylDAO[T, KeyType](table, None)

@Singleton
class StudentDAO extends ModelDAO[Student](students) {
  def byFileNumber(fileNumber: Int): Query[Student] =
    search(_.fileNumber === fileNumber)
}

@Singleton
class AdminDAO extends ModelDAO[Admin](admins) {
  def byFileNumber(fileNumber: Int): Query[Admin] =
    search(_.fileNumber === fileNumber)
}

@Singleton
class CareerDAO extends ModelDAO[Career](careers) {
  def byShortName(shortName: String): Query[Career] =
    search(_.shortName === shortName)

  def getOfAdmin(adminId: KeyType): Query[Career] =
    join(careers, adminsCareers)((c, ac) =>
      where(ac.adminId === adminId)
        select c
        on (c.id === ac.careerId)
    )
}

@Singleton
class SubjectDAO extends ModelDAO[Subject](subjects) {
  def byShortNameOfCareer(shortNames: Iterable[String], careerId: KeyType): Query[Subject] =
    search(_.careerId === careerId, _.shortName in shortNames)

  def getOfPoll(pollId: KeyType): Query[Subject] =
    join(subjects, pollOfferOptions)((s, poo) =>
      where(poo.pollId === pollId)
        select s
        on (poo.subjectId === s.id)
    ).distinct
}

@Singleton
class OfferDAO extends ModelDAO[OfferOptionBase](offers)

@Singleton
class CourseDAO extends ModelDAO[Course](courses) {
  def getOfPollBySubjectName(pollId: KeyType, subjectShortNames: Iterable[String]): Query[(Subject, Course, OfferOptionBase)] =
    join(courses, offers, pollOfferOptions, subjects)((c, o, poo, s) =>
      where(
        (o.isCourse === true) and
          (s.shortName in subjectShortNames) and
          (poo.pollId === pollId)
      ) select(s, c, o)
        on(c.id === o.offerId, o.id === poo.offerId, poo.subjectId === s.id)
    )

  def tallyByPoll(pollId: KeyType): Query[(Subject, Course, Student)] =
    join(courses, offers, pollSelectedOptions, results, subjects, students)((c, o, pso, r, sub, s) =>
      where(
        (o.isCourse === true) and
          (r.pollId === pollId)
      ) select(sub, c, s)
        on(
        c.id === o.offerId,
        o.id === pso.offerId,
        pso.pollResultId === r.id,
        pso.subjectId === s.id,
        r.studentId === s.id
      )
    )
}

@Singleton
class NonCourseDAO extends ModelDAO[NonCourseOption](nonCourses) {
  def byKey(textValues: Iterable[String]): Query[(NonCourseOption, OfferOptionBase)] =
    join(nonCourses, offers)((nc, o) =>
      where(
        (o.isCourse === false) and
          (nc.key in textValues)
      ) select(nc, o)
        on (o.offerId === nc.id)
    )

  def byKey(textValue: String): Query[(NonCourseOption, OfferOptionBase)] =
    join(nonCourses, offers)((nc, o) =>
      where(
        (o.isCourse === false) and
          (nc.key === textValue)
      ) select(nc, o)
        on (o.offerId === nc.id)
    )

  def getOfPollBySubjectName(pollId: KeyType, subjectShortNames: Iterable[String]): Query[(Subject, NonCourseOption, OfferOptionBase)] =
    join(nonCourses, offers, pollOfferOptions, subjects)((nc, o, poo, s) =>
      where(
        (o.isCourse === false) and
          (s.shortName in subjectShortNames) and
          (poo.pollId === pollId)
      ) select(s, nc, o)
        on(nc.id === o.offerId, o.id === poo.offerId, poo.subjectId === s.id)
    )

  def tallyByPoll(pollId: KeyType): Query[(Subject, NonCourseOption, Student)] =
    join(nonCourses, offers, pollSelectedOptions, results, subjects, students)((nc, o, pso, r, sub, s) =>
      where(
        (o.isCourse === false) and
          (r.pollId === pollId)
      ) select(sub, nc, s)
        on(
        nc.id === o.offerId,
        o.id === pso.offerId,
        pso.pollResultId === r.id,
        pso.subjectId === s.id,
        r.studentId === s.id
      )
    )
}

@Singleton
class ScheduleDAO extends ModelDAO[Schedule](schedules)

@Singleton
class PollDAO extends ModelDAO[Poll](polls) {
  def byKeyOfCareer(pollKey: String, careerId: KeyType): Query[Poll] =
    search(_.key === pollKey, _.careerId === careerId)

  def getOfCareer(carrerId: KeyType): Query[Poll] =
    search(_.careerId === carrerId)

  def getOfStudent(studentId: KeyType): Query[Poll] =
    join(polls, studentsCareers)((p, sc) =>
      where(sc.studentId === studentId)
        select p
        on (p.careerId === sc.careerId)
    )

  def getOfAdmin(adminId: KeyType): Query[Poll] =
    join(polls, adminsCareers)((p, ac) =>
      where(ac.adminId === adminId)
        select p
        on (p.careerId === ac.careerId)
    )
}

@Singleton
class PollResultDAO extends ModelDAO[PollResult](results) {
  def byStudentAndPoll(studentId: KeyType, pollId: KeyType): Query[PollResult] =
    search(_.studentId === studentId, _.pollId === pollId)
}

@Singleton
class PollSubjectOptionDAO extends ModelDAO[PollSubjectOption](pollSubjectOption)

@Singleton
class PollOfferOptionDAO extends ModelDAO[PollOfferOption](pollOfferOptions)

@Singleton
class PollSelectedOptionDAO extends ModelDAO[PollSelectedOption](pollSelectedOptions) {
  def getByPollAndOfPassedSubjectsOfStudent(pollResultId: KeyType, studentId: KeyType): Query[PollSelectedOption] = {
    val passedSubjects =
      join(subjects, pollSelectedOptions, results, offers, nonCourses)((sub, pso, r, o, nc) =>
        where(
          (r.studentId === studentId) and
            (o.isCourse === false) and
            (nc.key === NonCourseOption.alreadyPassed)
        ) select sub
          on(
          sub.id === pso.subjectId,
          pso.pollResultId === r.id,
          pso.offerId === o.id,
          o.offerId === nc.id
        )
      ).distinct
    join(pollSelectedOptions, results, passedSubjects)((pso, r, s) =>
      where(r.id === pollResultId)
        select pso
        on(pso.pollResultId === r.id, pso.subjectId === s.id)
    )
  }

  def getOfPollResult(pollResultId: KeyType, subjectShortNames: Iterable[String]): Query[(Subject, PollSelectedOption)] =
    join(pollSelectedOptions, subjects)((pso, sub) =>
      where(
        (sub.shortName in subjectShortNames) and
          (pso.pollResultId === pollResultId)
      ) select(sub, pso)
        on (pso.subjectId === sub.id)
    )
}

@Singleton
class StudentCareerDAO extends ModelDAO[StudentCareer](studentsCareers)

@Singleton
class AdminCareerDAO extends ModelDAO[AdminCareer](adminsCareers)
