package somind.dtlab.ingest.ingest.routes

import spray.json._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.pattern.ask
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.ingest.ingest.Conf._
import somind.dtlab.ingest.ingest.models._
import somind.dtlab.ingest.ingest.observe.Observer
import somind.dtlab.ingest.ingest.routes.functions.PostTelemetry

object ArrayIngestRoute
    extends LazyLogging
    with Directives
    with HttpSupport
    with JsonSupport {

  def apply: Route = {
    path("array" / Segment) { specId =>
      post {
        decodeRequest {
          entity(as[String]) { json =>
            onSuccess(objectExtractor ask (specId, json)) {
              case Some(m: Seq[(String, Telemetry)]) =>
                Observer("array_ingress_route_post_success")
                extractRequest { request =>
                  onSuccess(PostTelemetry(request, m)) {
                    case r: Seq[HttpResponse] =>
                      complete(StatusCodes.Accepted, m.toJson.prettyPrint)
                    case e =>
                      logger.warn(s"post to dtlab failed: $e")
                      complete(StatusCodes.InternalServerError)
                  }
                }
              case e =>
                Observer("array_ingress_route_post_unk_err")
                logger.warn(s"unable to handle: $e")
                complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    }
  }
}
