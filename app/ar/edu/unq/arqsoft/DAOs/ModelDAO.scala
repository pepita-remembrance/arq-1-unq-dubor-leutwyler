package ar.edu.unq.arqsoft.DAOs

import ar.edu.unq.arqsoft.database.DSLFlavor._
import ar.edu.unq.arqsoft.database.InscriptionPollSchema._
import ar.edu.unq.arqsoft.model.TableRow.KeyType
import ar.edu.unq.arqsoft.model._
import com.google.inject.Singleton
import org.squeryl.dsl.CompositeKey3
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
class CareerDAO extends ModelDAO[Career](careers) {
  def whereShortName(shortName: String): Query[Career] =
    where(_.shortName === shortName)
}

@Singleton
class SubjectDAO extends ModelDAO[Subject](subjects) {
  def subjectsOf(careerQuery: Query[Career]): Query[Subject] =
    join(careerQuery, subjects)((c, s) =>
      select(s)
        on (c.id === s.careerId)
    )

  def subjectsOf(careerQuery: Query[Career], shortNames: Iterable[String]): Query[Subject] =
    subjectsOf(careerQuery).where(_.shortName in shortNames)(dsl)

  def subjectsOf(pollQuery: Query[Poll])(implicit i1: DummyImplicit): Query[Subject] =
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
  def addBaseOfferOf(nonCourseQuery: Query[NonCourseOption]): Query[(NonCourseOption, OfferOptionBase)] =
    join(where(_.isCourse === false), nonCourseQuery)((o, nc) =>
      select((nc, o))
        on (o.offerId === nc.id)
    )

  def baseOfferOf(nonCourseQuery: Query[NonCourseOption]): Query[OfferOptionBase] =
    join(where(_.isCourse === false), nonCourseQuery)((o, nc) =>
      select(o)
        on (o.offerId === nc.id)
    )
}

@Singleton
class CourseDAO extends ModelDAO[Course](courses)

@Singleton
class NonCourseDAO extends ModelDAO[NonCourseOption](nonCourses) {
  val defaultOptionStrings = List("No voy a cursar", "Ya aprobe", "Ningun horario me sirve")

  def whereTextValue(textValue: String): Query[NonCourseOption] =
    where(_.textValue === textValue)

  def whereTextValue(textValues: Iterable[String]): Query[NonCourseOption] =
    where(_.textValue in textValues)

  def defaultOptions: Query[NonCourseOption] =
    whereTextValue(defaultOptionStrings)

  def notYetOption: Query[NonCourseOption] =
    whereTextValue(defaultOptionStrings(0))

  def alreadyPassedOption: Query[NonCourseOption] =
    whereTextValue(defaultOptionStrings(1))

  def noSuitableScheduleOption: Query[NonCourseOption] =
    whereTextValue(defaultOptionStrings(2))
}

@Singleton
class ScheduleDAO extends ModelDAO[Schedule](schedules)

@Singleton
class PollDAO extends ModelDAO[Poll](polls) {
  def pollsOf(careerQuery: Query[Career], pollKey: String): Query[Poll] =
    join(careerQuery, where(_.key === pollKey))((c, p) =>
      select(p)
        on (c.id === p.careerId)
    )

  def pollsOf(careerQuery: Query[Career]): Query[Poll] =
    join(careerQuery, polls)((c, p) =>
      select(p)
        on (c.id === p.careerId)
    )
}

@Singleton
class PollResultDAO extends ModelDAO[PollResult](results) {
  def resultsOf(studentQuery: Query[Student], pollQuery: Query[Poll]): Query[PollResult] =
    join(studentQuery, pollQuery, results)((s, p, r) =>
      select(r)
        on(r.studentId === s.id, r.pollId === p.id)
    )
}

@Singleton
class PollOfferOptionDAO extends SquerylDAO[PollOfferOption, CompositeKey3[KeyType, KeyType, KeyType]](pollOfferOptions, None)

@Singleton
class PollSelectedOptionDAO extends ModelDAO[PollSelectedOption](pollSelectedOptions) {
  def optionsOf(pollResultQuery: Query[PollResult]): Query[PollSelectedOption] =
    join(pollResultQuery, pollSelectedOptions)((pr, pso) =>
      select(pso)
        on (pso.pollResultId === pr.id)
    )

  def optionsOfWithSubject(pollResultQuery: Query[PollResult], subjectQuery: Query[Subject]): Query[PollSelectedOption] =
    join(pollResultQuery, subjectQuery, pollSelectedOptions)((pr, s, pso) =>
      select(pso)
        on(pso.pollResultId === pr.id, pso.subjectId === s.id)
    )

  def updateSelectionTo(options: Query[PollSelectedOption], newValue: KeyType): Int =
    dsl.update(pollSelectedOptions)(o =>
      dsl.where(o.id in from(options)(_o => select(_o.id)))
        set (o.offerId := newValue)
    )


}