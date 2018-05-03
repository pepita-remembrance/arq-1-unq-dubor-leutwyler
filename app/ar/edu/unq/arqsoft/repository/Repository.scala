package ar.edu.unq.arqsoft.repository

import ar.edu.unq.arqsoft.DAOs.SquerylDAO
import ar.edu.unq.arqsoft.database.DSLFlavor
import ar.edu.unq.arqsoft.logging.Logging
import ar.edu.unq.arqsoft.maybe._
import org.squeryl.dsl.ast.LogicalBoolean
import org.squeryl.{KeyedEntity, Query, SquerylSQLException}

import scala.util.Try

class Repository[T <: KeyedEntity[K], K](dao: SquerylDAO[T, K]) extends Logging {
  type SearchExpression = (T => LogicalBoolean)

  implicit protected def queryToIterable[A](query: Query[A]): Iterable[A] =
    DSLFlavor.queryToIterable(query)

  protected def inTransaction[A](a: => A): Maybe[A] =
    Try(DSLFlavor.inTransaction(a))

  protected def singleResult[A](query: Query[A], notFound: EntityNotFound): Maybe[A] = inTransaction(query.singleOption) match {
    case Something(Some(entity)) => Something(entity)
    case Something(None) => notFound
    case any: Nothing => any
  }

  protected def notFoundBy[A](property: String, value: A): EntityNotFound = EntityNotFound[A](dao.entityName, property, value)

  protected def notFoundById(key: K): EntityNotFound = notFoundBy("id", key)

  def all(): Maybe[Iterable[T]] = inTransaction {
    dao.all.toList
  }

  def find(key: K): Maybe[T] =
    singleResult(dao.find(key), notFoundById(key))

  def search(fields: SearchExpression*): Maybe[Iterable[T]] = inTransaction {
    dao.search(fields: _*).toList
  }

  def save(entity: T): Maybe[Unit] = inTransaction {
    dao.save(entity)
  }.recoverWith { case UnexpectedResult(_: SquerylSQLException) => SaveError.byName(dao.entityName) }


  def save(entities: Iterable[T], useBulk: Boolean = true): Maybe[Unit] =
    if (useBulk) {
      inTransaction(dao.save(entities))
        .recoverWith { case UnexpectedResult(_) => SaveError.byName(dao.entityName) }
    } else {
      entities.foldLeft(Something(()): Maybe[Unit]) { case (maybe, entity) =>
        maybe.flatMap(_ => save(entity))
      }
    }

  def update(entity: T): Maybe[Unit] = inTransaction {
    dao.update(entity)
  }.recoverWith { case UnexpectedResult(_: SquerylSQLException) => notFoundById(entity.id) }

  def update(entities: Iterable[T]): Maybe[Unit] = inTransaction {
    dao.update(entities)
  }

  def delete(key: K): Maybe[Unit] = inTransaction {
    dao.delete(key)
  } match {
    case Something(true) => Something(())
    case Something(false) => notFoundById(key)
    case any: Nothing => any
  }

  def delete(entity: T): Maybe[Unit] =
    delete(entity.id)
}
