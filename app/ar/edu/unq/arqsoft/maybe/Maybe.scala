package ar.edu.unq.arqsoft.maybe

import scala.util.{Failure, Success, Try}

trait Maybe[+A] {

  def get: A

  def isEmpty: Boolean

  def isDefined: Boolean = !isEmpty

  def getOrElse[B >: A](default: => B): B = if (isEmpty) default else get

  def orElse[B >: A](alternative: => Maybe[B]): Maybe[B] = if (isEmpty) alternative else this

  def map[B](f: A => B): Maybe[B]

  def flatMap[B](f: A => Maybe[B]): Maybe[B]

  def filter[B >: A](f: A => Boolean, alternative: Maybe[B]): Maybe[B] = if (isDefined && !f(get)) alternative else this

  def fold[B](ifEmpty: => B)(f: A => B): B = if (isEmpty) ifEmpty else f(this.get)

  def ifEmpty(f: => Unit): Maybe[A] = {
    if (isEmpty) f
    this
  }

  def forall(f: A => Boolean): Boolean = isEmpty || f(this.get)

  def foreach[U](f: A => U): Unit = if (!isEmpty) f(this.get)

  def perform[U](partial: PartialFunction[Maybe[A], U]): Unit = {
    if (partial isDefinedAt this) partial(this)
  }

  def exists(p: A => Boolean): Boolean = !isEmpty && p(this.get)

  def contains[B >: A](elem: B): Boolean = !isEmpty && this.get == elem

  def toList: List[A] = if (isEmpty) Nil else this.get :: Nil

  def toOption: Option[A] = if (isEmpty) None else Option(this.get)

  def recoverWith[B >: A](partial: PartialFunction[Maybe[A], Maybe[B]]): Maybe[B] = {
    if (partial isDefinedAt this) partial(this)
    else this
  }

  def recover[B >: A](f: => Maybe[B]): Maybe[B]

}

object Maybe {

  def fromOption[T](option: Option[T], nothing: => Nothing): Maybe[T] =
    option.map(Something(_)).getOrElse(nothing)

  implicit def try2Maybe[T](t: Try[T]): Maybe[T] = t match {
    case Success(k) => Something(k)
    case Failure(ex) => UnexpectedResult(ex)
  }

  implicit def try2UnitMaybe[T](t: Try[T]): Maybe[Unit] = t match {
    case Success(_) => Something(())
    case Failure(ex) => UnexpectedResult(ex)
  }

  implicit def try2UnitMaybe2(t: Try[Unit]): Maybe[Unit] = t match {
    case Success(_) => Something(())
    case Failure(ex) => UnexpectedResult(ex)
  }

  implicit class RichIterableMaybe[T](originalTraversable: Iterable[Maybe[T]]) {

    /**
      * Method to deal with Traversable[Maybe[T]], and turn it into Maybe[Traversable[T]]
      *
      * @return
      * * [Just]     Something(**EmptyTraversable**) if the original traversable was empty.
      * * [Just]     Something(Traversable[T]) if every Maybe in the original traversable was defined.
      * * [Just]     WithNothings(Traversable[T], List[Nothing]) in case some maybes are defined and some others are not.
      * * [Nothing]  AllNothings(Traversable[Nothing]) in case every maybe in the original traversable was empty.
      */
    def flattenMaybes: Maybe[Iterable[T]] = {
      // IDEA says this code does not compile but SBT compiles it just fine. IDEA bug?
      originalTraversable match {
        case seq if seq.forall(_.isDefined) =>
          Something(seq.map(_.get))
        case seq if seq.forall(_.isEmpty) =>
          AllNothings(seq.asInstanceOf[Iterable[Nothing]])
        case seq =>
          val (justs, nothings) = seq.partition(_.isDefined)
          WithNothings(justs.map(_.get), nothings.toList.asInstanceOf[List[Nothing]])
      }
    }
  }

}
