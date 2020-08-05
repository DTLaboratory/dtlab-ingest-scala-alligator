package somind.dtlab.ingest.ingest.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.pattern.ask
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.ingest.ingest.Conf._
import somind.dtlab.ingest.ingest.models.{JsonSupport, Telemetry}
import somind.dtlab.ingest.ingest.observe.Observer
import spray.json._

object TelemetryIngressRoute
    extends LazyLogging
    with Directives
    with HttpSupport
    with JsonSupport {

  def apply: Route = {
    path("telemetry" / Segment) { specId =>
      post {
        decodeRequest {
          entity(as[String]) { json =>
            onSuccess(telemetryExtractor ask (specId, json)) {
              case Some(telemetry: Seq[(String, Telemetry)] @unchecked) =>
                Observer("array_ingress_route_post_success")
                complete(StatusCodes.Accepted, telemetry.toJson.prettyPrint)
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
