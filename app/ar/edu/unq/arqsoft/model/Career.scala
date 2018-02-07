package ar.edu.unq.arqsoft.model

import ar.edu.unq.arqsoft.database.InscriptionPollSchema
import ar.edu.unq.arqsoft.model.TableRow.KeyType

case class Career(shortName: String, longName: String) extends TableRow {
  lazy val subjects = InscriptionPollSchema.careerSubjects.left(this)
  lazy val polls = InscriptionPollSchema.careerPolls.left(this)
  lazy val students = InscriptionPollSchema.studentsCareers.right(this)
}

case class Subject(careerId: KeyType, shortName: String, longName: String) extends TableRow

case class Course(key: String, quota: Int, offerId: KeyType) extends TableRow with OfferOption {
  lazy val schedules = InscriptionPollSchema.courseSchedules.left(this)

  def isCourse: Boolean = true
}
