package ar.edu.unq.arqsoft.DAOs

import javax.inject.Singleton

import ar.edu.unq.arqsoft.database.InscriptionPollSchema._
import ar.edu.unq.arqsoft.database.DSLFlavor._
import ar.edu.unq.arqsoft.model._
import ar.edu.unq.arqsoft.model.TableRow.KeyType
import org.squeryl.dsl.CompositeKey3
import org.squeryl.{CanLookup, KeyedEntityDef, Table}

class ModelDAO[T](table: Table[T])
                 (implicit ked: KeyedEntityDef[T, KeyType], toCanLookup: KeyType => CanLookup)
  extends SquerylDAO[T, KeyType](table, None)

@Singleton
class StudentDAO extends ModelDAO[Student](students)

@Singleton
class CareerDAO extends ModelDAO[Career](careers)

@Singleton
class SubjectDAO extends ModelDAO[Subject](subjects)

@Singleton
class OfferDAO extends ModelDAO[OfferOptionBase](offers)

@Singleton
class CourseDAO extends ModelDAO[Course](courses)

@Singleton
class NonCourseDAO extends ModelDAO[NonCourseOption](nonCourses)

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