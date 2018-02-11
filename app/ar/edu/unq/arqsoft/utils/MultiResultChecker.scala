package ar.edu.unq.arqsoft.utils

import ar.edu.unq.arqsoft.maybe._

class MultiResultChecker[A, B](expectations: Iterable[B], tester: Iterable[A] => B => Option[EntityNotFound])
  extends (Iterable[A] => Maybe[Iterable[A]]) {
  def apply(results: Iterable[A]): Maybe[Iterable[A]] = {
    val errors = expectations.flatMap(tester(results)(_))
    if (errors.isEmpty) {
      Something(results)
    } else {
      NotFounds(errors)
    }
  }
}
