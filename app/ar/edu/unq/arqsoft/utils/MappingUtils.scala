package ar.edu.unq.arqsoft.utils

import ar.edu.unq.arqsoft.database.DSLFlavor._
import org.squeryl.Query

object MappingUtils {

  implicit class QueryConverter[A](query: Query[A]) {
    def mapAs[B](implicit fun: A => B): Iterable[B] = query.map(fun)

    def computeCount: Long = from(query)(q => compute(count)).single.measures
  }

  implicit class IterableConverter[A](iterable: Iterable[A]) {
    def mapAs[B](implicit fun: A => B): Iterable[B] = iterable.map(fun)
  }

}
