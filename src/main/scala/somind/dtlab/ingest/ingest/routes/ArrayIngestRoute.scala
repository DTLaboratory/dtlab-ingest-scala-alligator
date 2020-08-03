package somind.dtlab.ingest.ingest.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.pattern.ask
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.ingest.ingest.Conf._
import somind.dtlab.ingest.ingest.models.{ExtractorErr, ExtractorOk}
import somind.dtlab.ingest.ingest.observe.Observer

object ArrayIngestRoute extends LazyLogging with Directives with HttpSupport {

  def apply: Route = {
    path("array" / Segment) { specId =>
      post {
        decodeRequest {
          entity(as[String]) { json =>
            onSuccess(objectExtractor ask (specId, json)) {
              case _: ExtractorOk =>
                Observer("array_ingress_route_post_success")
                complete(StatusCodes.Accepted)
              case ExtractorErr(message) =>
                Observer("array_ingress_route_post_error")
                complete(StatusCodes.NotAcceptable, message)
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
