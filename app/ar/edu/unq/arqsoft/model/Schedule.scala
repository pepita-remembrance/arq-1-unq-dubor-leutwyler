package ar.edu.unq.arqsoft.model

import ar.edu.unq.arqsoft.model.TableRow.KeyType

case class Schedule(courseId: KeyType, day: Int, fromHour: Int, fromMinutes: Int, toHour: Int, toMinutes: Int) extends TableRow

sealed abstract class Day(value: Int, name: String)

case object Monday extends Day(0, "Lunes")

case object Tuesday extends Day(1, "Martes")

case object Wednesday extends Day(2, "Miercoles")

case object Thursday extends Day(3, "Jueves")

case object Friday extends Day(4, "Viernes")

case object Saturday extends Day(5, "Sabado")

case object Sunday extends Day(6, "Domingo")
