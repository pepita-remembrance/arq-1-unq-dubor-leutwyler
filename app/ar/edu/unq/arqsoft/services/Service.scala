package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api.OutputDTO
import ar.edu.unq.arqsoft.logging.Logging
import ar.edu.unq.arqsoft.mappings.dto.DTOMappings
import ar.edu.unq.arqsoft.maybe.Maybe
import ar.edu.unq.arqsoft.model.TableRow

trait Service extends DTOMappings with Logging {

  implicit class RichMaybe[In <: TableRow](maybe: Maybe[In]) {
    def as[Out <: OutputDTO](implicit fun: In => Out): Maybe[Out] =
      maybe.map(fun)
  }

  implicit class RichMaybeIterable[In <: TableRow](maybe: Maybe[Iterable[In]]) {
    def mapAs[Out <: OutputDTO](implicit fun: In => Out): Maybe[Iterable[Out]] =
      maybe.map(_.map(fun))
  }

  implicit class RichIterable[In](iterable: Iterable[In]) {
    def mapAs[Out](implicit fun: In => Out): Iterable[Out] =
      iterable.map(fun)
  }

  implicit class RichTableRow[In <: TableRow](row: In) {
    def as[Out <: OutputDTO](implicit fun: In => Out): Out =
      fun(row)
  }
}
