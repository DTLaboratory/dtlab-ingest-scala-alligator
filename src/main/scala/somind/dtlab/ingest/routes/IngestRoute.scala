package somind.dtlab.ingest.routes

import akka.actor.ActorRef
import akka.http.scaladsl.server._
import akka.pattern.ask
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.ingest.Conf._
import somind.dtlab.ingest.models.JsonSupport

object IngestRoute
    extends LazyLogging
    with Directives
    with HttpSupport
    with JsonSupport {

  def apply(pathName: String, extractor: ActorRef): Route = {
    path(pathName / Segment) { specId =>
      post {
        decodeRequest {
          entity(as[String]) { json =>
            onSuccess(extractor ask (specId, json)) {
              PostTelemetryRoute.apply
            }
          }
        }
      }
    }
  }

}
