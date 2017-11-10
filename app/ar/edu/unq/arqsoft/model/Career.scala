package ar.edu.unq.arqsoft.model

import ar.edu.unq.arqsoft.database.Database
import ar.edu.unq.arqsoft.model.TableRow.KeyType

case class Career(shortName: String, longName: String) extends TableRow {
  lazy val subjects = Database.careerSubjects.left(this)
  lazy val polls = Database.careerPolls.left(this)
}

case class Subject(careerId: KeyType, shortName: String, longName: String) extends TableRow

case class Course(shortName: String) extends TableRow with OfferOption {
  lazy val schedules = Database.courseSchedules.left(this)

  val isCourse: Boolean = true

  val textValue: String = shortName
}
