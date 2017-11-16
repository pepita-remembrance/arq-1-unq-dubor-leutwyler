package ar.edu.unq.arqsoft.DAOs

import javax.inject.Singleton

import ar.edu.unq.arqsoft.database.InscriptionPollSchema._
import ar.edu.unq.arqsoft.database.DSLFlavor._
import ar.edu.unq.arqsoft.model._
import ar.edu.unq.arqsoft.model.TableRow.KeyType
import org.squeryl.dsl.CompositeKey3
import org.squeryl.{CanLookup, KeyedEntityDef, Query, Table}

class ModelDAO[T](table: Table[T])
                 (implicit ked: KeyedEntityDef[T, KeyType], toCanLookup: KeyType => CanLookup)
  extends SquerylDAO[T, KeyType](table, None)

@Singleton
class StudentDAO extends ModelDAO[Student](students) {
  def whereFileNumber(fileNumber: Int): Query[Student] = where(_.fileNumber === fileNumber)
}

@Singleton
class CareerDAO extends ModelDAO[Career](careers) {
  def whereShortName(shortName: String): Query[Career] = where(_.shortName === shortName)
}

@Singleton
class SubjectDAO extends ModelDAO[Subject](subjects)

@Singleton
class OfferDAO extends ModelDAO[OfferOptionBase](offers) {
  def courses: Query[OfferOptionBase] = where(_.isCourse === true)

  def nonCourses: Query[OfferOptionBase] = where(_.isCourse === false)
}

@Singleton
class CourseDAO extends ModelDAO[Course](courses)

@Singleton
class NonCourseDAO extends ModelDAO[NonCourseOption](nonCourses) {
  def whereTextValue(textValue: String): Query[NonCourseOption] = where(_.textValue === textValue)

  def whereTextValueIn(textValues: Iterable[String]): Query[NonCourseOption] = where(_.textValue in textValues)
}

@Singleton
class ScheduleDAO extends ModelDAO[Schedule](schedules)

@Singleton
class PollDAO extends ModelDAO[Poll](polls)

@Singleton
class PollResultDAO extends ModelDAO[PollResult](results)

@Singleton
class PollOfferOptionDAO extends SquerylDAO[PollOfferOption, CompositeKey3[KeyType, KeyType, KeyType]](pollOfferOptions, None)

@Singleton
class PollSelectedOptionDAO extends SquerylDAO[PollSelectedOption, CompositeKey3[KeyType, KeyType, KeyType]](pollSelectedOptions, None)