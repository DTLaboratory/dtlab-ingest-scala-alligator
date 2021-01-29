package dtlaboratory.dtlab.ingest.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{Directives, Route}
import com.typesafe.scalalogging.LazyLogging
import dtlaboratory.dtlab.ingest.models._
import dtlaboratory.dtlab.ingest.observe.Observer
import dtlaboratory.dtlab.ingest.routes.functions.PostTelemetry
import spray.json._

/**
 * Formatted data enters here and is posted to DTLab.
 */
object PostTelemetryRoute
    extends LazyLogging
    with Directives
    with HttpSupport
    with JsonSupport {

  def apply(in: Any): Route = {
    in match {
      case Some(telemetry: Seq[(String, Telemetry)] @unchecked) =>
        Observer("array_ingress_route_post_success")
        onSuccess(PostTelemetry(telemetry)) {
          case r: Seq[HttpResponse] @unchecked =>
            val errors = r.filter(_.status != StatusCodes.Accepted)
            if (errors.nonEmpty) {
              logger.warn(s"can not post telemetry: ${errors.head}")
              complete(
                HttpResponse(
                  errors.head.status
                )
              )
            } else
              complete(
                HttpResponse(
                  StatusCodes.Accepted,
                  entity = HttpEntity(ContentTypes.`application/json`,
                                      telemetry.toJson.prettyPrint)
                )
              )
          case e =>
            logger.warn(s"post dtlab failed: $e")
            complete(StatusCodes.InternalServerError)
        }
      case _: ExtractorNoData =>
        Observer("array_ingress_route_post_array_no_data_extracted")
        complete(StatusCodes.BadRequest)
      case None =>
        Observer("array_ingress_route_post_array_no_spec_found")
        complete(StatusCodes.NotFound)
      case e =>
        Observer("array_ingress_route_post_unk_err")
        logger.warn(s"unable to handle: $e")
        complete(StatusCodes.InternalServerError)
    }
  }

}
