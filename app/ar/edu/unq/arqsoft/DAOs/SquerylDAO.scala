package ar.edu.unq.arqsoft.DAOs

import org.squeryl.dsl.QueryDsl
import org.squeryl.dsl.ast.{LogicalBoolean, TrueLogicalBoolean}
import org.squeryl.internals.FieldReferenceLinker.{createEqualityExpressionWithLastAccessedFieldReferenceAndConstant => createEqualityExpression}
import org.squeryl._

abstract class SquerylDAO[T, K](table: Table[T], _entityName: Option[String])(implicit val dsl: QueryDsl, val ked: KeyedEntityDef[T, K], toCanLookup: K => CanLookup) {

  def entityName: String = _entityName.getOrElse(table.name)

  def all: Query[T] =
    dsl.from(table)(entity => dsl.select(entity))

  def find(key: K): Query[T] =
    all.where(entity => createEqualityExpression(ked.getId(entity), key, toCanLookup(key)))

  def search(fields: (T => LogicalBoolean)*): Query[T] =
    all.where(
      fields.fold((entity => TrueLogicalBoolean): T => LogicalBoolean) {
        case (expr1, expr2) => entity: T => expr1(entity).and(expr2(entity))
      }
    )

  def save(entity: T): Unit =
    table.insert(entity)

  def save(entities: Iterable[T]): Unit =
  //    table.insert(entities)
    entities.foreach(save)

  def update(entity: T): Unit =
    table.update(entity)

  def delete(key: K): Boolean =
    table.delete(key)
}