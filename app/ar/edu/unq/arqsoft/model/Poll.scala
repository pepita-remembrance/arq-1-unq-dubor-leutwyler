package ar.edu.unq.arqsoft.model

import ar.edu.unq.arqsoft.database.InscriptionPollSchema
import ar.edu.unq.arqsoft.model.TableRow.KeyType
import org.joda.time.DateTime
import org.squeryl.{KeyedEntity, Query}
import org.squeryl.annotations.Transient
import org.squeryl.dsl.CompositeKey3
import ar.edu.unq.arqsoft.database.DSLFlavor._

case class Poll(key: String, careerId: KeyType, isOpen: Boolean) extends TableRow {
  lazy val career = InscriptionPollSchema.careerPolls.right(this)
  lazy val offers = InscriptionPollSchema.pollPollOfferOptions.left(this)
}

case class OfferOptionBase(offerId: KeyType, isCourse: Boolean) extends TableRow {
  def discriminated: Query[OfferOption] = {
    if (isCourse) {
      from(InscriptionPollSchema.courses)(c => where(c.id === offerId) select c)
    } else {
      from(InscriptionPollSchema.nonCourses)(c => where(c.id === offerId) select c)
    }
  }
}

object OfferOptionBase {
  def apply(course: Course): OfferOptionBase = new OfferOptionBase(course.id, true)

  def apply(nonCourse: NonCourseOption): OfferOptionBase = new OfferOptionBase(nonCourse.id, false)
}

case class PollOfferOption(pollId: KeyType, subjectId: KeyType, offerId: KeyType)
  extends KeyedEntity[CompositeKey3[KeyType, KeyType, KeyType]] {
  lazy val poll = InscriptionPollSchema.pollPollOfferOptions.right(this)
  lazy val subject = InscriptionPollSchema.subjectPollOfferOptions.right(this)
  lazy val offer = InscriptionPollSchema.offerPollOfferOptions.right(this)

  override def id: CompositeKey3[KeyType, KeyType, KeyType] = compositeKey(pollId, subjectId, offerId)
}

case class PollResult(pollId: KeyType, studentId: KeyType, fillDate: DateTime) extends TableRow {
  lazy val poll = InscriptionPollSchema.pollResults.right(this)
  lazy val student = InscriptionPollSchema.studentResults.right(this)
  lazy val selectedOptions = InscriptionPollSchema.resultPollSelectedOptions.left(this)
}

case class PollSelectedOption(pollResultId: KeyType, subjectId: KeyType, offerId: KeyType)
  extends KeyedEntity[CompositeKey3[KeyType, KeyType, KeyType]] {
  override def id: CompositeKey3[KeyType, KeyType, KeyType] = compositeKey(pollResultId, subjectId, offerId)
}

trait OfferOption extends KeyedEntity[KeyType] {
  this: TableRow =>
  @transient
  @Transient
  val isCourse: Boolean

  @transient
  @Transient
  val textValue: String

  def base: Query[OfferOptionBase] = from(InscriptionPollSchema.offers)(baseOffer => where(baseOffer.isCourse === isCourse and baseOffer.offerId === id) select baseOffer)
}

case class NonCourseOption(textValue: String) extends TableRow with OfferOption {
  val isCourse: Boolean = false
}

object DefaultOption {
  val notYet = NonCourseOption("Aun no voy a cursar")
  val alreadyPassed = NonCourseOption("Ya aprobe")
  val noSuitableCourse = NonCourseOption("Ningun horario me sirve")
  val defaults = List(
    notYet,
    alreadyPassed,
    noSuitableCourse
  )
}
