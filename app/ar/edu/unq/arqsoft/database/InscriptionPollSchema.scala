package ar.edu.unq.arqsoft.database

import java.sql.Timestamp

import org.joda.time.DateTime
import org.squeryl.dsl._
import org.squeryl.{PrimitiveTypeMode, Schema}

trait PrimitiveJodaTimeSupport {
  this: PrimitiveTypeMode =>
  implicit val jodaTimeTEF = new NonPrimitiveJdbcMapper[Timestamp, DateTime, TTimestamp](timestampTEF, this) {

    def convertFromJdbc(t: Timestamp) = new DateTime(t)

    def convertToJdbc(t: DateTime) = new Timestamp(t.getMillis)
  }

  implicit val optionJodaTimeTEF =
    new TypedExpressionFactory[Option[DateTime], TOptionTimestamp]
      with DeOptionizer[Timestamp, DateTime, TTimestamp, Option[DateTime], TOptionTimestamp] {

      val deOptionizer = jodaTimeTEF
    }

  implicit def jodaTimeToTE(s: DateTime): TypedExpression[DateTime, TTimestamp] = jodaTimeTEF.create(s)

  implicit def optionJodaTimeToTE(s: Option[DateTime]): TypedExpression[Option[DateTime], TOptionTimestamp] = optionJodaTimeTEF.create(s)
}

trait DSLFlavor extends PrimitiveTypeMode with PrimitiveJodaTimeSupport

object DSLFlavor extends DSLFlavor

abstract class InscriptionPollSchema extends Schema()(DSLFlavor.thisFieldMapper) with DSLFlavor {



}
