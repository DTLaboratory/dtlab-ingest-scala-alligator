package somind.dtlab.ingest.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.pattern.ask
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.ingest.Conf._
import somind.dtlab.ingest.observe.Observer

object TelemetryIngressRoute extends LazyLogging with Directives with HttpSupport {

  def apply: Route = {
    path("telemetry" / Segment) { specId =>
      post {
        decodeRequest {
          entity(as[String]) { json =>
            onSuccess(objectExtractor ask (specId, json)) { e =>
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
