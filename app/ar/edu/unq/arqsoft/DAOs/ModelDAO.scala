package ar.edu.unq.arqsoft.DAOs

import ar.edu.unq.arqsoft.database.DSLFlavor._
import ar.edu.unq.arqsoft.database.InscriptionPollSchema._
import ar.edu.unq.arqsoft.model.TableRow.KeyType
import ar.edu.unq.arqsoft.model._
import com.google.inject.Singleton
import org.squeryl.{CanLookup, KeyedEntityDef, Query, Table}

class ModelDAO[T](table: Table[T])
                 (implicit ked: KeyedEntityDef[T, KeyType], toCanLookup: KeyType => CanLookup)
  extends SquerylDAO[T, KeyType](table, None)

@Singleton
class StudentDAO extends ModelDAO[Student](students) {
  def whereFileNumber(fileNumber: Int): Query[Student] =
    where(_.fileNumber === fileNumber)
}

@Singleton
class AdminDAO extends ModelDAO[Admin](admins) {
  def whereFileNumber(fileNumber: Int): Query[Admin] =
    where(_.fileNumber === fileNumber)
}

@Singleton
class CareerDAO extends ModelDAO[Career](careers) {
  def whereShortName(shortName: String): Query[Career] =
    where(_.shortName === shortName)

  def careersOfAdmin(adminCareerQuery: Query[AdminCareer]): Query[Career] =
    join(adminCareerQuery, careers)((ac, c) =>
      select(c)
        on (ac.careerId === c.id)
    )
}

@Singleton
class SubjectDAO extends ModelDAO[Subject](subjects) {
  def subjectsOfCareer(careerQuery: Query[Career]): Query[Subject] =
    join(careerQuery, subjects)((c, s) =>
      select(s)
        on (c.id === s.careerId)
    )

  def subjectsOfCareerWithName(careerQuery: Query[Career], shortNames: Iterable[String]): Query[Subject] =
    subjectsOfCareer(careerQuery).where(_.shortName in shortNames)(dsl)

  def subjectsOfPoll(pollQuery: Query[Poll])(implicit i1: DummyImplicit): Query[Subject] =
    join(pollQuery, pollOfferOptions, subjects)((p, poo, s) =>
      select(s)
        on(s.id === poo.subjectId, p.id === poo.pollId)
    ).distinct

  def subjectsWithOption(selectedOptionsQuery: Query[PollSelectedOption], wantedOption: Query[OfferOptionBase], subjectQuery: Query[Subject]): Query[Subject] =
    join(selectedOptionsQuery, wantedOption, subjectQuery)((s, w, sub) =>
      select(sub)
        on(s.subjectId === sub.id, s.offerId === w.offerId)
    ).distinct


}

@Singleton
class OfferDAO extends ModelDAO[OfferOptionBase](offers) {
  def addBaseOfferOfCourse(courseQuery: Query[Course]): Query[(Course, OfferOptionBase)] =
    join(where(_.isCourse === true), courseQuery)((o, c) =>
      select((c, o))
        on (o.offerId === c.id)
    )

  def baseOfferOfCourse(courseQuery: Query[Course]): Query[OfferOptionBase] =
    join(where(_.isCourse === true), courseQuery)((o, c) =>
      select(o)
        on (o.offerId === c.id)
    )

  def addBaseOfferOfNonCourse(nonCourseQuery: Query[NonCourseOption])(implicit i1: DummyImplicit): Query[(NonCourseOption, OfferOptionBase)] =
    join(where(_.isCourse === false), nonCourseQuery)((o, nc) =>
      select((nc, o))
        on (o.offerId === nc.id)
    )

  def baseOfferOfNonCourse(nonCourseQuery: Query[NonCourseOption])(implicit i1: DummyImplicit): Query[OfferOptionBase] =
    join(where(_.isCourse === false), nonCourseQuery)((o, nc) =>
      select(o)
        on (o.offerId === nc.id)
    )

  def baseOfferOfPollOfferWithSubject(pollOfferOptionQuery: Query[PollOfferOption], subjectQuery: Query[Subject]): Query[(Subject, OfferOptionBase)] =
    join(pollOfferOptionQuery, subjectQuery, offers)((poo, s, o) =>
      select(s, o)
        on(poo.offerId === o.id, poo.subjectId === s.id)
    )
}

@Singleton
class CourseDAO extends ModelDAO[Course](courses) {
  def addCoursesOfSubjectOffer(query: Query[(Subject, OfferOptionBase)]): Query[(Subject, Course, OfferOptionBase)] =
  // Workarround to select from tuples
    join(query, subjects, offers, courses)((tuple, s, o, c) =>
      dsl.where(tuple._2.isCourse === true)
        select(s, c, o)
        on(tuple._2.offerId === c.id, tuple._1.id === s.id, tuple._2.id === o.id)
    )

}

@Singleton
class NonCourseDAO extends ModelDAO[NonCourseOption](nonCourses) {
  val defaultOptionStrings = List("No voy a cursar", "Ya aprobe", "Ningun horario me sirve")

  def whereTextValue(textValue: String): Query[NonCourseOption] =
    where(_.key === textValue)

  def whereTextValue(textValues: Iterable[String]): Query[NonCourseOption] =
    where(_.key in textValues)

  def defaultOptions: Query[NonCourseOption] =
    whereTextValue(defaultOptionStrings)

  def notYetOption: Query[NonCourseOption] =
    whereTextValue(defaultOptionStrings(0))

  def alreadyPassedOption: Query[NonCourseOption] =
    whereTextValue(defaultOptionStrings(1))

  def noSuitableScheduleOption: Query[NonCourseOption] =
    whereTextValue(defaultOptionStrings(2))

  def addNonCoursesOfSubjectOffer(query: Query[(Subject, OfferOptionBase)]): Query[(Subject, NonCourseOption, OfferOptionBase)] =
  // Workarround to select from tuples
    join(query, subjects, offers, nonCourses)((tuple, s, o, nc) =>
      dsl.where(tuple._2.isCourse === false)
        select(s, nc, o)
        on(tuple._2.offerId === nc.id, tuple._1.id === s.id, tuple._2.id === o.id)
    )
}

@Singleton
class ScheduleDAO extends ModelDAO[Schedule](schedules)

@Singleton
class PollDAO extends ModelDAO[Poll](polls) {
  def pollsOfCareerWithKey(careerQuery: Query[Career], pollKey: String): Query[Poll] =
    join(careerQuery, where(_.key === pollKey))((c, p) =>
      select(p)
        on (c.id === p.careerId)
    )

  def pollsOfCareer(careerQuery: Query[Career]): Query[Poll] =
    join(careerQuery, polls)((c, p) =>
      select(p)
        on (c.id === p.careerId)
    )

  def pollsOfStudent(studentCareerQuery: Query[StudentCareer]): Query[Poll] =
    join(studentCareerQuery, polls)((sc, p) =>
      dsl.where(p.createDate gte sc.joinDate)
        select (p)
        on (sc.careerId === p.careerId)
    )

  def pollsOfAdmin(adminCareerQuery: Query[AdminCareer]): Query[Poll] =
    join(adminCareerQuery, polls)((admin, p) =>
        select (p)
        on (admin.careerId === p.careerId)
    )
}

@Singleton
class PollResultDAO extends ModelDAO[PollResult](results) {
  def resultsOfStudentForPoll(studentQuery: Query[Student], pollQuery: Query[Poll]): Query[PollResult] =
    join(studentQuery, pollQuery, results)((s, p, r) =>
      select(r)
        on(r.studentId === s.id, r.pollId === p.id)
    )
}

@Singleton
class PollOfferOptionDAO extends ModelDAO[PollOfferOption](pollOfferOptions) {
  def optionsOfPoll(pollQuery: Query[Poll]): Query[PollOfferOption] =
    join(pollQuery, pollOfferOptions)((p, poo) =>
      select(poo)
        on (poo.pollId === p.id)
    )
}

@Singleton
class PollSelectedOptionDAO extends ModelDAO[PollSelectedOption](pollSelectedOptions) {
  def optionsOfPoll(pollResultQuery: Query[PollResult]): Query[PollSelectedOption] =
    join(pollResultQuery, pollSelectedOptions)((pr, pso) =>
      select(pso)
        on (pso.pollResultId === pr.id)
    )

  def optionsOfPollWithSubject(pollResultQuery: Query[PollResult], subjectQuery: Query[Subject]): Query[PollSelectedOption] =
    join(pollResultQuery, subjectQuery, pollSelectedOptions)((pr, s, pso) =>
      select(pso)
        on(pso.pollResultId === pr.id, pso.subjectId === s.id)
    )

  def addOptionsOfPollWithSubject(pollResultQuery: Query[PollResult], subjectQuery: Query[Subject]): Query[(Subject, PollSelectedOption)] =
    join(pollResultQuery, subjectQuery, pollSelectedOptions)((pr, s, pso) =>
      select(s, pso)
        on(pso.pollResultId === pr.id, pso.subjectId === s.id)
    )

  def updateSelectionTo(options: Query[PollSelectedOption], newValue: KeyType): Int =
    dsl.update(pollSelectedOptions)(o =>
      dsl.where(o.id in from(options)(_o => select(_o.id)))
        set (o.offerId := newValue)
    )
}

@Singleton
class StudentCareerDAO extends ModelDAO[StudentCareer](studentsCareers) {
  def whereStudent(studentQuery: Query[Student]): Query[StudentCareer] =
    join(studentQuery, studentsCareers)((s, sc) =>
      select(sc)
        on (s.id === sc.studentId)
    )
}

@Singleton
class AdminCareerDAO extends ModelDAO[AdminCareer](adminsCareers) {
  def whereAdmin(adminQuery: Query[Admin]): Query[AdminCareer] =
    join(adminQuery, adminsCareers)((a, ac) =>
      select(ac)
        on (a.id === ac.adminId)
    )
}