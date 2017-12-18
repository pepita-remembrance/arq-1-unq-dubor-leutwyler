package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.DAOs._
import ar.edu.unq.arqsoft.database.DSLFlavor
import ar.edu.unq.arqsoft.logging.Logging
import ar.edu.unq.arqsoft.mappings.dto.DTOMappings
import org.squeryl.Query

trait Service extends DTOMappings with DAOBindings with Logging {

  def inTransaction[A](a: => A): A = DSLFlavor.inTransaction(a)

  implicit def queryToIterable[A](query: Query[A]): Iterable[A] = DSLFlavor.queryToIterable(query)

}
