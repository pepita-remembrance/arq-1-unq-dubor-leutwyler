package ar.edu.unq.arqsoft.controllers.httpFilters

import akka.stream.Materializer
import ar.edu.unq.arqsoft.logging.Logging
import com.google.inject.Inject
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.{ExecutionContext, Future}

class LoggingFilter @Inject()(implicit val mat: Materializer, ec: ExecutionContext) extends Filter with Logging {

  protected val ignoredRoutes: List[String] = Nil

  def apply(nextFilter: RequestHeader => Future[Result])
           (request: RequestHeader): Future[Result] = {

    val startTime = System.currentTimeMillis

    log(s"START - ${request.method} ${request.uri}")

    nextFilter(request).map { result =>

      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime

      log(s"END - ${request.method} ${request.uri} took $requestTime ms and returned ${result.header.status}")

      result.withHeaders("Request-Time" -> requestTime.toString)
    }
  }

  def log(message: String): Unit =
    if (!ignoredRoutes.exists(message.contains(_))) {
      info(message)
    }

}
