package ar.edu.unq.arqsoft.DAOs

import com.google.inject.Singleton
import ar.edu.unq.arqsoft.database.InscriptionPollSchema._
import ar.edu.unq.arqsoft.database.DSLFlavor._
import ar.edu.unq.arqsoft.database.InscriptionPollSchema
import ar.edu.unq.arqsoft.model._
import ar.edu.unq.arqsoft.model.TableRow.KeyType
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
  def whereCareerAndShortNameIn(career: Career, shortNames: Iterable[String]): Query[Subject] =
    career.subjects.where(_.shortName in shortNames)(dsl)
}

@Singleton
class OfferDAO extends ModelDAO[OfferOptionBase](offers) {
  def baseOffer: NonCourseOption ==> (NonCourseOption, OfferOptionBase) = ncs =>
    join(where(_.isCourse === false), ncs)((o, nc) =>
      select((nc, o))
        on (o.offerId === nc.id)
    )
}

@Singleton
class CourseDAO extends ModelDAO[Course](courses)

@Singleton
class NonCourseDAO extends ModelDAO[NonCourseOption](nonCourses) {
  def whereTextValue(textValues: Iterable[String]): Query[NonCourseOption] =
    where(_.textValue in textValues)
}

@Singleton
class ScheduleDAO extends ModelDAO[Schedule](schedules)

@Singleton
class PollDAO extends ModelDAO[Poll](polls)

@Singleton
class PollResultDAO extends ModelDAO[PollResult](results) {
  def studentResults: Student ==> PollResult = studentQuery =>
    join(studentQuery, results)((s, r) =>
      select(r)
        on (s.id === r.studentId)
    )
}

@Singleton
class PollOfferOptionDAO extends SquerylDAO[PollOfferOption, CompositeKey3[KeyType, KeyType, KeyType]](pollOfferOptions, None)

@Singleton
class PollSelectedOptionDAO extends SquerylDAO[PollSelectedOption, CompositeKey3[KeyType, KeyType, KeyType]](pollSelectedOptions, None)