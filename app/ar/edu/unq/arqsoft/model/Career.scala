package ar.edu.unq.arqsoft.model

import ar.edu.unq.arqsoft.model.TableRow.KeyType

case class Career(shortName: String, longName: String) extends TableRow

case class Subject(careerId: KeyType, shortName: String, longName: String) extends TableRow

case class Course(shortName: String) extends TableRow with OfferOption {
  val isCourse: Boolean = true

  val textValue: String = shortName
}
