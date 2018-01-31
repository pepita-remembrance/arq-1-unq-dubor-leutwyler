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

case class UnexpectedResult(obj:Any) extends Nothing
