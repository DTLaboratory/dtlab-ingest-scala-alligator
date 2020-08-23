package somind.dtlab.ingest.routes

import akka.http.scaladsl.server._
import akka.pattern.ask
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.ingest.Conf._
import somind.dtlab.ingest.models.JsonSupport
import somind.dtlab.ingest.routes.functions.PostTelemetryRoute

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
              PostTelemetryRoute.apply
            }
          }
        }
      }
    }
  }
}
