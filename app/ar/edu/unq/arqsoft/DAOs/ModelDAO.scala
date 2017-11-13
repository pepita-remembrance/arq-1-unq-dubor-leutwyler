package ar.edu.unq.arqsoft.DAOs

import javax.inject.Singleton

import ar.edu.unq.arqsoft.database.Database._
import ar.edu.unq.arqsoft.model._
import ar.edu.unq.arqsoft.model.TableRow.KeyType
import org.squeryl.dsl.QueryDsl
import org.squeryl.{CanLookup, KeyedEntityDef, Table}

class ModelDAO[T](table: Table[T])
                 (implicit dsl: QueryDsl, ked: KeyedEntityDef[T, KeyType], toCanLookup: KeyType => CanLookup)
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
class PollOfferOptionDAO extends ModelDAO[PollOfferOption](pollOfferOptions)

@Singleton
class PollResultDAO extends ModelDAO[PollResult](results)

@Singleton
class PollSelectedOptionDAO extends ModelDAO[PollSelectedOption](pollSelectedOptions)