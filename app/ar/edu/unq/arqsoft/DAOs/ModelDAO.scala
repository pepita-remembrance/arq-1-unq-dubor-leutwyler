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
      dsl.where(ac.adminId === adminId)
        select c
        on (c.id === ac.careerId)
    )

  //DELETE!
  def careersOfAdmin(fileNumber: Int): Query[Career] =
    join(careers, adminsCareers, admins)((c, ac, a) =>
      dsl.where(a.fileNumber === fileNumber)
        select c
        on(c.id === ac.careerId, ac.adminId === a.id)
    )
}

@Singleton
class SubjectDAO extends ModelDAO[Subject](subjects) {
  def byShortNameOfCareer(shortNames: Iterable[String], careerId: KeyType): Query[Subject] =
    search(_.careerId === careerId, _.shortName in shortNames)

  def getOfPoll(pollId: KeyType): Query[Subject] =
    join(subjects, pollOfferOptions)((s, poo) =>
      where(poo.id === pollId)
        select s
        on (poo.subjectId === s.id)
    ).distinct

  //DELETE!
  def subjectsOfCareerWithName(careerShortName: String, shortNames: Iterable[String]): Query[Subject] =
    join(subjects, careers)((s, c) =>
      dsl.where(
        (c.shortName === careerShortName) and
          (s.shortName in shortNames)
      ) select s
        on (s.careerId === c.id)
    )

  //DELETE!
  def subjectsOfPoll(careerShortName: String, pollKey: String): Query[Subject] =
    join(subjects, pollOfferOptions, polls, careers)((s, poo, p, c) =>
      dsl.where((p.key === pollKey) and (c.shortName === careerShortName))
        select s
        on(s.id === poo.subjectId, poo.pollId === p.id, p.careerId === c.id)
    ).distinct
}

@Singleton
class OfferDAO extends ModelDAO[OfferOptionBase](offers) {
  //DELETE!
  def baseOfferOfNonCourse(textValues: Iterable[String]): Query[OfferOptionBase] =
    join(offers, nonCourses)((o, nc) =>
      dsl.where(
        (o.isCourse === false) and
          (nc.key in textValues)
      ) select o
        on (o.offerId === nc.id)
    )

  //DELETE!
  def baseOfferOfNonCourse(textValue: String): Query[OfferOptionBase] =
    join(offers, nonCourses)((o, nc) =>
      dsl.where(
        (o.isCourse === false) and
          (nc.key === textValue)
      ) select o
        on (o.offerId === nc.id)
    )
}

@Singleton
class CourseDAO extends ModelDAO[Course](courses) {
  def getOfPollBySubjectName(pollId: KeyType, subjectShortNames: Iterable[String]): Query[(Subject, Course, OfferOptionBase)] =
    join(courses, offers, pollOfferOptions, subjects)((c, o, poo, s) =>
      dsl.where(
        (o.isCourse === true) and
          (s.shortName in subjectShortNames) and
          (poo.pollId === pollId)
      ) select(s, c, o)
        on(c.id === o.offerId, o.id === poo.offerId, poo.subjectId === s.id)
    )

  def tallyByPoll(pollId: KeyType): Query[(Subject, Course, Student)] =
    join(courses, offers, pollSelectedOptions, results, subjects, students)((c, o, pso, r, sub, s) =>
      dsl.where(
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

  //DELETE!
  def coursesForSubjectsOfPollWithBaseOffer(subjectShortNames: Iterable[String], careerShortName: String, pollKey: String): Query[(Subject, Course, OfferOptionBase)] =
    join(courses, offers, pollOfferOptions, polls, careers, subjects)((c, o, poo, p, career, s) =>
      dsl.where(
        (o.isCourse === true) and
          (s.shortName in subjectShortNames) and
          (career.shortName === careerShortName) and
          (p.key === pollKey)
      ) select(s, c, o)
        on(c.id === o.offerId, o.id === poo.offerId, poo.pollId === p.id, p.careerId === career.id, poo.subjectId === s.id)
    )

  //DELETE!
  def tallyForPoll(careerShortName: String, pollKey: String): Query[(Subject, Course, Student)] =
    join(courses, offers, pollSelectedOptions, results, polls, careers, subjects, students)((c, o, pso, r, p, career, sub, s) =>
      dsl.where(
        (o.isCourse === true) and
          (p.key === pollKey) and
          (career.shortName === careerShortName)
      ) select(sub, c, s)
        on(
        c.id === o.offerId,
        o.id === pso.offerId,
        pso.pollResultId === r.id,
        r.pollId === p.id,
        p.careerId === career.id,
        pso.subjectId === s.id,
        r.studentId === s.id
      )
    )
}

@Singleton
class NonCourseDAO extends ModelDAO[NonCourseOption](nonCourses) {
  def byKey(textValues: Iterable[String]): Query[(NonCourseOption, OfferOptionBase)] =
    join(nonCourses, offers)((nc, o) =>
      dsl.where(
        (o.isCourse === false) and
          (nc.key in textValues)
      ) select(nc, o)
        on (o.offerId === nc.id)
    )

  def byKey(textValue: String): Query[(NonCourseOption, OfferOptionBase)] =
    join(nonCourses, offers)((nc, o) =>
      dsl.where(
        (o.isCourse === false) and
          (nc.key === textValue)
      ) select(nc, o)
        on (o.offerId === nc.id)
    )

  def getOfSubjectByPoll(subjectShortNames: Iterable[String], pollId: KeyType): Query[(Subject, NonCourseOption, OfferOptionBase)] =
    join(nonCourses, offers, pollOfferOptions, subjects)((nc, o, poo, s) =>
      dsl.where(
        (o.isCourse === true) and
          (s.shortName in subjectShortNames) and
          (poo.pollId === pollId)
      ) select(s, nc, o)
        on(nc.id === o.offerId, o.id === poo.offerId, poo.subjectId === s.id)
    )

  def tallyByPoll(pollId: KeyType): Query[(Subject, NonCourseOption, Student)] =
    join(nonCourses, offers, pollSelectedOptions, results, subjects, students)((nc, o, pso, r, sub, s) =>
      dsl.where(
        (o.isCourse === true) and
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

  //DELETE!
  def whereKey(textValue: String): Query[NonCourseOption] =
    search(_.key === textValue)

  //DELETE!
  def whereKey(textValues: Iterable[String]): Query[NonCourseOption] =
    search(_.key in textValues)

  //DELETE!
  def whereTextValueWithBaseOffer(textValues: Iterable[String]): Query[(NonCourseOption, OfferOptionBase)] =
    join(nonCourses, offers)((nc, o) =>
      dsl.where(
        (nc.key in textValues) and
          (o.isCourse === false)
      )
        select(nc, o)
        on (nc.id === o.offerId)
    )

  //DELETE!
  def nonCoursesForSubjectsOfPollWithBaseOffer(subjectShortNames: Iterable[String], careerShortName: String, pollKey: String): Query[(Subject, NonCourseOption, OfferOptionBase)] =
    join(nonCourses, offers, pollOfferOptions, polls, careers, subjects)((nc, o, poo, p, career, s) =>
      dsl.where(
        (o.isCourse === false) and
          (s.shortName in subjectShortNames) and
          (career.shortName === careerShortName) and
          (p.key === pollKey)
      ) select(s, nc, o)
        on(nc.id === o.offerId, o.id === poo.offerId, poo.pollId === p.id, p.careerId === career.id, poo.subjectId === s.id)
    )

  //DELETE!
  def tallyForPoll(careerShortName: String, pollKey: String): Query[(Subject, NonCourseOption, Student)] =
    join(nonCourses, offers, pollSelectedOptions, results, polls, careers, subjects, students)((nc, o, pso, r, p, career, sub, s) =>
      dsl.where(
        (o.isCourse === false) and
          (p.key === pollKey) and
          (career.shortName === careerShortName)
      ) select(sub, nc, s)
        on(
        nc.id === o.offerId,
        o.id === pso.offerId,
        pso.pollResultId === r.id,
        r.pollId === p.id,
        p.careerId === career.id,
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
      dsl.where(sc.studentId === studentId)
        select p
        on (p.careerId === sc.careerId)
    )

  def getOfAdmin(adminId: KeyType): Query[Poll] =
    join(polls, adminsCareers)((p, ac) =>
      dsl.where(ac.adminId === adminId)
        select p
        on (p.careerId === ac.careerId)
    )

  //DELETE!
  def pollByCareerAndKey(careerShortName: String, pollKey: String): Query[Poll] =
    join(polls, careers)((p, c) =>
      dsl.where(
        (p.key === pollKey) and
          (c.shortName === careerShortName)
      ) select p
        on (p.careerId === c.id)
    )

  //DELETE!
  def pollsOfCareer(careerShortName: String): Query[Poll] =
    join(polls, careers)((p, c) =>
      dsl.where(c.shortName === careerShortName)
        select p
        on (p.careerId === c.id)
    )

  //DELETE!
  def pollsOfStudent(studentFileNumber: Int): Query[Poll] =
    join(polls, studentsCareers, students)((p, sc, s) =>
      dsl.where(s.fileNumber === studentFileNumber)
        select p
        on(p.careerId === sc.careerId, sc.studentId === s.id)
    )

  //DELETE!
  def pollsOfAdmin(adminFileNumber: Int): Query[Poll] =
    join(polls, adminsCareers, admins)((p, ac, a) =>
      dsl.where(a.fileNumber === adminFileNumber)
        select p
        on(p.careerId === ac.careerId, ac.adminId === a.id)
    )
}

@Singleton
class PollResultDAO extends ModelDAO[PollResult](results) {
  def byStudentAndPoll(studentId: KeyType, pollId: KeyType): Query[PollResult] =
    search(_.studentId === studentId, _.pollId === pollId)

  //DELETE!
  def resultByStudentAndPoll(studentFileNumber: Int, careerShortName: String, pollKey: String): Query[PollResult] =
    join(results, students, polls, careers)((r, s, p, c) =>
      dsl.where(
        (s.fileNumber === studentFileNumber) and
          (p.key === pollKey) and
          (c.shortName === careerShortName)
      ) select r
        on(r.studentId === s.id, r.pollId === p.id, p.careerId === c.id)
    )
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
        dsl.where(
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
      dsl.where(r.id === pollResultId)
        select pso
        on(pso.pollResultId === r.id, pso.subjectId === s.id)
    )
  }

  def getOfPollResult(pollResultId: KeyType, subjectShortNames: Iterable[String]): Query[(Subject, PollSelectedOption)] =
    join(pollSelectedOptions, subjects)((pso, sub) =>
      dsl.where(
        (sub.shortName in subjectShortNames) and
          (pso.pollResultId === pollResultId)
      ) select(sub, pso)
        on (pso.subjectId === sub.id)
    )

  //DELETE!
  def optionsByPollResultAndPassedSubjectsOfStudent(pollResultId: KeyType, studentFileNumber: Int): Query[PollSelectedOption] = {
    val passedSubjects =
      join(subjects, pollSelectedOptions, results, students, offers, nonCourses)((sub, pso, r, s, o, nc) =>
        dsl.where(
          (s.fileNumber === studentFileNumber) and
            (o.isCourse === false) and
            (nc.key === NonCourseOption.alreadyPassed)
        ) select sub
          on(
          sub.id === pso.subjectId,
          pso.pollResultId === r.id,
          r.studentId === s.id,
          pso.offerId === o.id,
          o.offerId === nc.id
        )
      ).distinct
    join(pollSelectedOptions, results, passedSubjects)((pso, r, s) =>
      dsl.where(r.id === pollResultId)
        select pso
        on(pso.pollResultId === r.id, pso.subjectId === s.id)
    )
  }

  //DELETE!
  def optionsByPollResultAndSubjectsWithSubject(studentFileNumber: Int, careerShortName: String, pollKey: String, subjectShortNames: Iterable[String]): Query[(Subject, PollSelectedOption)] =
    join(pollSelectedOptions, subjects, results, polls, careers, students)((pso, sub, r, p, c, s) =>
      dsl.where(
        (sub.shortName in subjectShortNames) and
          (p.key === pollKey) and
          (c.shortName === careerShortName) and
          (s.fileNumber === studentFileNumber)
      ) select(sub, pso)
        on(pso.subjectId === sub.id, pso.pollResultId === r.id, r.pollId === p.id, p.careerId === c.id, r.studentId === s.id)
    )

  //DELETE!
  def updateSelectionTo(options: Query[PollSelectedOption], newValue: KeyType): Int =
    dsl.update(pollSelectedOptions)(o =>
      dsl.where(o.id in from(options)(_o => select(_o.id)))
        set (o.offerId := newValue)
    )
}

@Singleton
class StudentCareerDAO extends ModelDAO[StudentCareer](studentsCareers)

@Singleton
class AdminCareerDAO extends ModelDAO[AdminCareer](adminsCareers)
