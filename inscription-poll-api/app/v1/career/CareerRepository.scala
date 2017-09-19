package v1.career

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

final case class CareerData(id: Long, name: String, description: String)

class CareerId private(val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object CareerId {
  def apply(raw: String): CareerId = {
    require(raw != null)
    new CareerId(Integer.parseInt(raw))
  }
}


class CareerExecutionContext @Inject()(actorSystem: ActorSystem) extends CustomExecutionContext(actorSystem, "repository.dispatcher")

/**
  * A pure non-blocking interface for the CareerRepository.
  */
trait CareerRepository {
  def create(data: CareerData)(implicit mc: MarkerContext): Future[Long]

  def list()(implicit mc: MarkerContext): Future[Iterable[CareerData]]

  def get(id: Long)(implicit mc: MarkerContext): Future[Option[CareerData]]

  def delete(id: Long)(implicit mc: MarkerContext): Future[Int]
}

/**
  * A trivial implementation for the Career Repository.
  *
  * A custom execution context is used here to establish that blocking operations should be
  * executed in a different thread than Play's ExecutionContext, which is used for CPU bound tasks
  * such as rendering.
  */
@Singleton
class CareerRepositoryImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: CareerExecutionContext) extends CareerRepository
  with HasDatabaseConfigProvider[JdbcProfile]
  with CareerTableDef{
  import profile.api._
  private val logger = Logger(this.getClass)

  def careerList = TableQuery[CareerTable]

  override def list()(implicit mc: MarkerContext): Future[Iterable[CareerData]] = {
    logger.trace(s"list: ")
    db.run(careerList.result)
  }

  override def get(id: Long)(implicit mc: MarkerContext): Future[Option[CareerData]] = {
    logger.trace(s"get: id = $id")
    db.run(careerList.filter(career => career.id === id).result.headOption)
  }

  def create(data: CareerData)(implicit mc: MarkerContext): Future[Long] = {
    logger.trace(s"create: data = $data")
    db.run(careerList returning careerList.map(_.id) += data)
  }

  def delete(id: Long)(implicit mc: MarkerContext): Future[Int] = {
    logger.trace(s"remove: $id")
    db.run(careerList.filter(_.id === id).delete)
  }
}
