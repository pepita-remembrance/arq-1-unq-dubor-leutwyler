package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.DAOs._
import ar.edu.unq.arqsoft.database.DSLFlavor
import ar.edu.unq.arqsoft.logging.Logging
import ar.edu.unq.arqsoft.mappings.dto.DTOMappings
import ar.edu.unq.arqsoft.maybe.{EntityNotFound, Maybe, Something, UnexpectedResult}
import org.squeryl.Query

import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}

trait Service extends DTOMappings with DAOBindings with Logging {

  def inTransaction[A](a: => Maybe[A]): Maybe[A] = Try(DSLFlavor.inTransaction(a)) match {
    case Success(maybe) => maybe
    case Failure(exception) => UnexpectedResult(exception)
  }

  implicit def queryToIterable[A](query: Query[A]): Iterable[A] = DSLFlavor.queryToIterable(query)

  implicit class RichQuery[In](query: Query[In])(implicit tagA: ClassTag[In]) {
    def orNotFoundWith[K](property: String, value: K): Maybe[In] = query.singleOption match {
      case Some(entity) => Something(entity)
      case None => EntityNotFound(tagA.runtimeClass.getSimpleName, property, value)
    }

    def mapAs[Out](implicit fun: In => Out): Maybe[Iterable[Out]] =
      Something(query.map(fun))
  }

  implicit class RichIterable[In](iterable: Iterable[In]) {
    def mapAs[Out](implicit fun: In => Out): Maybe[Iterable[Out]] =
      Something(iterable.map(fun))
  }

  implicit class RichMaybe[In](maybe: Maybe[In]) {
    def as[Out](implicit fun: In => Out): Maybe[Out] =
      maybe.map(fun)
  }

}
