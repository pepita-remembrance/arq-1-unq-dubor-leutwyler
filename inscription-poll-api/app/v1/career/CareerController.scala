package v1.career

import javax.inject.Inject

import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

case class CareerFormInput(name: String, description: String)

/**
  * Takes HTTP requests and produces JSON.
  */
class CareerController @Inject()(cc: CareerControllerComponents)(implicit ec: ExecutionContext)
    extends CareerBaseController(cc) {


  private val logger = Logger(getClass)

  private val form: Form[CareerFormInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "name" -> nonEmptyText,
        "description" -> text
      )(CareerFormInput.apply)(CareerFormInput.unapply)
    )
  }

  def index: Action[AnyContent] = CareerAction.async { implicit request =>
    logger.trace("index: ")
    careerResourceHandler.find.map { careers =>
      Ok(Json.toJson(careers))
    }
  }

  def process: Action[AnyContent] = CareerAction.async { implicit request =>
    logger.trace("process: ")
    processJsonCareer()
  }

  def show(id: String): Action[AnyContent] = CareerAction.async { implicit request =>
    logger.trace(s"show: id = $id")
    careerResourceHandler.lookup(id).map { career =>
      Ok(Json.toJson(career))
    }
  }

  def delete(id: String): Action[AnyContent] = CareerAction.async { implicit request =>
    logger.trace(s"delete: id = $id")
    careerResourceHandler.delete(id).map { career =>
      Ok(Json.toJson(career))
    }
  }

  private def processJsonCareer[A]()(implicit request: CareerRequest[A]): Future[Result] = {
    def failure(badForm: Form[CareerFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: CareerFormInput) = {
      careerResourceHandler.create(input).map { career =>
        Created(Json.toJson(career)).withHeaders(LOCATION -> career.link)
      }
    }

    form.bindFromRequest().fold(failure, success)
  }
}
