package ar.edu.unq.arqsoft.model

import ar.edu.unq.arqsoft.model.Day.Day
import ar.edu.unq.arqsoft.model.TableRow.KeyType

case class Schedule(courseId: KeyType, day: Day, fromHour: Int, fromMinutes: Int, toHour: Int, toMinutes: Int) extends TableRow

object Day extends Enumeration {
  type Day = Value

  val Monday = Value(0)
  val Tuesday = Value(1)
  val Wednesday = Value(2)
  val Thursday = Value(3)
  val Friday = Value(4)
  val Saturday = Value(5)
  val Sunday = Value(6)
}
