package ar.edu.unq.arqsoft.model

import ar.edu.unq.arqsoft.database.Database
import ar.edu.unq.arqsoft.model.TableRow.KeyType
import org.joda.time.DateTime
import org.squeryl.{KeyedEntity, Query}
import org.squeryl.annotations.Transient
import org.squeryl.dsl.CompositeKey3
import Database._

case class Poll(key: String, careerId: KeyType, isOpen: Boolean) extends TableRow

case class OfferOptionBase(offerId: KeyType, isCourse: Boolean) extends TableRow {
  def discriminated: Query[OfferOption] = {
    if (isCourse) {
      from(courses)(c => where(c.id === offerId) select c)
    } else {
      from(nonCourses)(c => where(c.id === offerId) select c)
    }
  }
}

case class PollOfferOption(pollId: KeyType, subjectId: KeyType, offerId: KeyType)
  extends KeyedEntity[CompositeKey3[KeyType, KeyType, KeyType]] {
  override def id: CompositeKey3[KeyType, KeyType, KeyType] = Database.compositeKey(pollId, subjectId, offerId)
}

case class PollResult(pollId: KeyType, studentId: KeyType, fillDate: DateTime) extends TableRow

case class PollSelectedOption(pollResultId: KeyType, subjectId: KeyType, offerId: KeyType)
  extends KeyedEntity[CompositeKey3[KeyType, KeyType, KeyType]] {
  override def id: CompositeKey3[KeyType, KeyType, KeyType] = compositeKey(pollResultId, subjectId, offerId)
}

trait OfferOption {
  @transient
  @Transient
  val isCourse: Boolean

  @transient
  @Transient
  val textValue: String
}

case class NonCourseOption(textValue: String) extends TableRow with OfferOption {
  val isCourse: Boolean = false
}

object NotYet extends NonCourseOption("Aun no voy a cursar")

object AlreadyPassed extends NonCourseOption("Ya aprobe")

object NoSuitableCourse extends NonCourseOption("Ningun horario me sirve")
