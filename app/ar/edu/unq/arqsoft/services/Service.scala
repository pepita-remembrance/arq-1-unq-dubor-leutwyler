package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.database.DSLFlavor
import ar.edu.unq.arqsoft.logging.Logging
import ar.edu.unq.arqsoft.mappings.dto.DTOMappings

trait Service[T] extends DTOMappings with Logging {

  def inTransaction[A](a: => A): A = DSLFlavor.inTransaction(a)

}
