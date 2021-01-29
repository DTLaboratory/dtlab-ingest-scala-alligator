package dtlaboratory.dtlab.ingest.routes

import akka.actor.ActorRef
import akka.http.scaladsl.server._
import akka.pattern.ask
import com.typesafe.scalalogging.LazyLogging
import dtlaboratory.dtlab.ingest.models.JsonSupport
import dtlaboratory.dtlab.ingest.Conf._

object IngestRoute
    extends LazyLogging
    with Directives
    with HttpSupport
    with JsonSupport {

  /**
   * unformatted data enters here, is converted to formatted observations, and
   * is passed on to the route that poses to DTLab
   * @param pathName API route should be something like "array" or "object"
   * @param extractor implementation that pulls formatted observations out of the
   *                  raw input.  An implementation specializes in syntax and
   *                  dimensions, json array, xml object, csv table, etc...
   * @return
   */
  def apply(pathName: String, extractor: ActorRef): Route = {
    path(pathName / Segment) { specId =>
      post {
        withoutRequestTimeout {
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

}
