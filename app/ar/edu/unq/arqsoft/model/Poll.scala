package ar.edu.unq.arqsoft.model

import ar.edu.unq.arqsoft.database.Database
import ar.edu.unq.arqsoft.model.TableRow.KeyType
import org.joda.time.DateTime
import org.squeryl.{KeyedEntity, Query}
import org.squeryl.annotations.Transient
import org.squeryl.dsl.CompositeKey3
import Database._

case class Poll(key: String, careerId: KeyType, isOpen: Boolean) extends TableRow {
  lazy val career = careerPolls.right(this)
  lazy val offers = pollPollOfferOptions.left(this)
}

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
  lazy val poll = pollPollOfferOptions.right(this)
  lazy val subject = subjectPollOfferOptions.right(this)
  lazy val offer = offerPollOfferOptions.right(this)

  override def id: CompositeKey3[KeyType, KeyType, KeyType] = compositeKey(pollId, subjectId, offerId)
}

case class PollResult(pollId: KeyType, studentId: KeyType, fillDate: DateTime) extends TableRow {
  lazy val poll = pollResults.right(this)
  lazy val student = studentResults.right(this)
  lazy val selectedOptions = resultPollSelectedOptions.left(this)
}

case class PollSelectedOption(pollResultId: KeyType, subjectId: KeyType, offerId: KeyType)
  extends KeyedEntity[CompositeKey3[KeyType, KeyType, KeyType]] {
  override def id: CompositeKey3[KeyType, KeyType, KeyType] = compositeKey(pollResultId, subjectId, offerId)
}

trait OfferOption {
  this: TableRow =>
  @transient
  @Transient
  val isCourse: Boolean

  @transient
  @Transient
  val textValue: String

  def base: Query[OfferOptionBase] = from(offers)(baseOffer => where(baseOffer.isCourse === isCourse and baseOffer.offerId === id) select baseOffer)
}

case class NonCourseOption(textValue: String) extends TableRow with OfferOption {
  val isCourse: Boolean = false
}

object NotYet extends NonCourseOption("Aun no voy a cursar")

object AlreadyPassed extends NonCourseOption("Ya aprobe")

object NoSuitableCourse extends NonCourseOption("Ningun horario me sirve")
