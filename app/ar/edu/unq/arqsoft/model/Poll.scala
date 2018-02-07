package ar.edu.unq.arqsoft.model

import ar.edu.unq.arqsoft.database.InscriptionPollSchema
import ar.edu.unq.arqsoft.model.TableRow.KeyType
import org.joda.time.DateTime

case class Poll(key: String, careerId: KeyType, isOpen: Boolean, createDate: DateTime) extends TableRow {
  lazy val career = InscriptionPollSchema.careerPolls.right(this)
  lazy val offers = InscriptionPollSchema.pollPollOfferOptions.left(this)
  lazy val results = InscriptionPollSchema.pollResults.left(this)
  lazy val extraData = InscriptionPollSchema.pollPollSubjectOptions.left(this)
}

case class OfferOptionBase(isCourse: Boolean) extends TableRow

object OfferOptionBase {
  def forCourse: OfferOptionBase = this.apply(isCourse = true)

  def forNonCourse: OfferOptionBase = this.apply(isCourse = false)
}

case class PollSubjectOption(pollId: KeyType, subjectId: KeyType, extraData: String) extends TableRow

case class PollOfferOption(pollId: KeyType, subjectId: KeyType, offerId: KeyType) extends TableRow

case class PollResult(pollId: KeyType, studentId: KeyType, var fillDate: DateTime) extends TableRow {
  lazy val poll = InscriptionPollSchema.pollResults.right(this)
  lazy val student = InscriptionPollSchema.studentResults.right(this)
  lazy val selectedOptions = InscriptionPollSchema.resultPollSelectedOptions.left(this)
}

case class PollSelectedOption(pollResultId: KeyType, subjectId: KeyType, var offerId: KeyType) extends TableRow

trait OfferOption {
  def key: String

  def offerId: KeyType

  def isCourse:Boolean
}

case class NonCourseOption(key: String, offerId: KeyType) extends TableRow with OfferOption {
  def isCourse: Boolean = false
}

object NonCourseOption {
  val notYet = "No voy a cursar"
  val alreadyPassed = "Ya aprobe"
  val noSuitableSchedule = "Ningun horario me sirve"
  val defaultOptionStrings = List(notYet, alreadyPassed, noSuitableSchedule)
}