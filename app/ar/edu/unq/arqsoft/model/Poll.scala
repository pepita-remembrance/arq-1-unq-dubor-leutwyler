package ar.edu.unq.arqsoft.model

import ar.edu.unq.arqsoft.database.DSLFlavor._
import ar.edu.unq.arqsoft.database.InscriptionPollSchema
import ar.edu.unq.arqsoft.model.TableRow.KeyType
import org.joda.time.DateTime
import org.squeryl.{KeyedEntity, Query}

case class Poll(key: String, careerId: KeyType, isOpen: Boolean, createDate: DateTime) extends TableRow {
  lazy val career = InscriptionPollSchema.careerPolls.right(this)
  lazy val offers = InscriptionPollSchema.pollPollOfferOptions.left(this)
  lazy val results = InscriptionPollSchema.pollResults.left(this)
  lazy val extraData = InscriptionPollSchema.pollPollSubjectOptions.left(this)
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

case class PollSubjectOption(pollId: KeyType, subjectId: KeyType, extraData: String) extends TableRow

case class PollOfferOption(pollId: KeyType, subjectId: KeyType, offerId: KeyType) extends TableRow {
  lazy val offer = InscriptionPollSchema.offerPollOfferOptions.right(this)
}

case class PollResult(pollId: KeyType, studentId: KeyType, var fillDate: DateTime) extends TableRow {
  lazy val poll = InscriptionPollSchema.pollResults.right(this)
  lazy val student = InscriptionPollSchema.studentResults.right(this)
  lazy val selectedOptions = InscriptionPollSchema.resultPollSelectedOptions.left(this)
}

case class PollSelectedOption(pollResultId: KeyType, subjectId: KeyType, var offerId: KeyType) extends TableRow

trait OfferOption extends KeyedEntity[KeyType] {
  this: TableRow =>

  def key: String
}

case class NonCourseOption(key: String) extends TableRow with OfferOption

object NonCourseOption {
  val notYet = "No voy a cursar"
  val alreadyPassed = "Ya aprobe"
  val noSuitableSchedule = "Ningun horario me sirve"
  val defaultOptionStrings = List(notYet, alreadyPassed, noSuitableSchedule)
}