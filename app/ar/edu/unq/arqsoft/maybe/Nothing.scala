package ar.edu.unq.arqsoft.maybe

trait Nothing extends Maybe[scala.Nothing] {

  def isEmpty: Boolean = true

  def get: scala.Nothing = throw new NoSuchElementException("Nothing.get")

  def map[B](f: (scala.Nothing) => B): Maybe[B] = this

  def flatMap[B](f: (scala.Nothing) => Maybe[B]): Maybe[B] = this

  def recover[B](f: => Maybe[B]): Maybe[B] = f
}

object Nothing {
  def unapply(maybe: Maybe[_]): Option[String] = maybe match {
    case x: Nothing => Some(x.toString)
    case _ => None
  }
}

trait Message {
  def message: String
}

case class EntityNotFound(message: String) extends Nothing with Message

object EntityNotFound {
  def apply[V](entityName: String, property: String, value: V): EntityNotFound =
    EntityNotFound(s"$entityName with $property valued $value not found")
}

case class SaveError(message: String) extends Nothing with Message

object SaveError {
  def byName(entityName: String): SaveError = SaveError(s"Error saving $entityName")
}

case class UnexpectedResult(obj: Any) extends Nothing

trait MultiNothing[N <: Nothing] extends Nothing {
  def nothings: Iterable[N]
}

case class AllNothings(nothings: Iterable[Nothing]) extends MultiNothing[Nothing]

case class NotFounds(nothings: Iterable[EntityNotFound]) extends MultiNothing[EntityNotFound] with Message {
  def message: String =
    nothings.map(_.message).mkString("\n")
}

object BadLogin extends Nothing with Message {
  def message: String = s"Username or password is incorrect"
}

object CompareError extends Nothing