package somind.dtlab.ingest.routes.functions

import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.ingest.models.{ExtractorNoData, JsonSupport, Telemetry}
import somind.dtlab.ingest.observe.Observer
import somind.dtlab.ingest.routes.HttpSupport
import spray.json._

object PostTelemetryRoute
    extends LazyLogging
    with Directives
    with HttpSupport
    with JsonSupport {

  def apply(in: Any): Route = {
    in match {
      case Some(telemetry: Seq[(String, Telemetry)] @unchecked) =>
        Observer("array_ingress_route_post_success")
        extractRequest { request =>
          onSuccess(PostTelemetry(request, telemetry)) {
            case _: Seq[HttpResponse] @unchecked =>
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
        }
      case _: ExtractorNoData =>
        Observer("array_ingress_route_post_array_no_data_extracted")
        complete(StatusCodes.OK)
      case e =>
        Observer("array_ingress_route_post_unk_err")
        logger.warn(s"unable to handle: $e")
        complete(StatusCodes.BadRequest)
    }
  }

}
