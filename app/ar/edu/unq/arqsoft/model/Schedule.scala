package ar.edu.unq.arqsoft.model

import ar.edu.unq.arqsoft.model.TableRow.KeyType

case class Schedule(courseId: KeyType, day: Int, fromHour: Int, fromMinutes: Int, toHour: Int, toMinutes: Int) extends TableRow

sealed abstract class Day(value: Int)

case object Monday extends Day(0)

case object Tuesday extends Day(1)

case object Wednesday extends Day(2)

case object Thursday extends Day(3)

case object Friday extends Day(4)

case object Saturday extends Day(5)

case object Sunday extends Day(6)
