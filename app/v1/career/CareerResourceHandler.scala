package v1.career

import scala.language.postfixOps
import javax.inject.{Inject, Provider}

import play.api.MarkerContext

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

/**
  * DTO for displaying career information.
  */
case class careerResource(id: String, link: String, name: String, description: String)

object careerResource {

  /**
    * Mapping to write a PostResource out as a JSON value.
    */
  implicit val implicitWrites = new Writes[careerResource] {
    def writes(career: careerResource): JsValue = {
      Json.obj(
        "id" -> career.id,
        "link" -> career.link,
        "name" -> career.name,
        "description" -> career.description
      )
    }
  }
}

/**
  * Controls access to the backend data, returning [[careerResource]]
  */
class CareerResourceHandler @Inject()(
                                       routerProvider: Provider[CareerRouter],
                                       careerRepository: CareerRepository)(implicit ec: ExecutionContext) {

  def create(careerInput: CareerFormInput)(implicit mc: MarkerContext): Future[careerResource] = {
    val data = CareerData(999, careerInput.name, careerInput.description)
    careerRepository.create(data).map { id =>
      createCareerResource(data.copy(id = id))
    }
  }

  def lookup(id: String)(implicit mc: MarkerContext): Future[Option[careerResource]] = {
    val careerFuture = careerRepository.get(id toLong)
    careerFuture.map { maybeCareerData =>
      maybeCareerData.map { careerData =>
        createCareerResource(careerData)
      }
    }
  }

  def find(implicit mc: MarkerContext): Future[Iterable[careerResource]] = {
    careerRepository.list().map { careerDataList =>
      careerDataList.map(careerData => createCareerResource(careerData))
    }
  }

  def delete(id: String)(implicit mc: MarkerContext) : Future[Long] = {
    careerRepository.delete(id toLong).map(res => res toLong)
  }

  private def createCareerResource(career: CareerData): careerResource = {
    careerResource(
      career.id.toString, routerProvider.get.link(career.id),
      career.name, career.description)
  }

}
