package ar.edu.unq.arqsoft.maybe

trait Just[A] extends Maybe[A] {

  def isEmpty: Boolean = false

  def flatMap[B](f: (A) => Maybe[B]): Maybe[B] = f(this.get)

  def recover[B >: A](f: => Maybe[B]): Maybe[B] = this

}

object Just {
  def unapply[T](maybe: Maybe[T]): Option[T] = maybe match {
    case x: Just[T] => Some(x.get)
    case _ => None
  }
}

case class Something[A](get:A) extends Just[A] {
  def map[B](f: (A) => B): Maybe[B] = Something(f(get))
}

case class WithNothings[A](get: A,
                           nothings: List[Nothing]) extends Just[A] {

  def map[B](f: (A) => B): Maybe[B] = WithNothings(f(get), nothings)
}