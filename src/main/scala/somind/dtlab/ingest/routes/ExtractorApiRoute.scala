package somind.dtlab.ingest.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.pattern.ask
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.ingest.Conf._
import somind.dtlab.ingest.models._
import somind.dtlab.ingest.observe.Observer
import spray.json._

/**
 * Extractor API lets you define how to turn raw json input into DtLab telemetry messages
 */
object ExtractorApiRoute
    extends JsonSupport
    with LazyLogging
    with Directives
    with HttpSupport {

  def apply: Route = {
    path("extractor" / Segment) { specId =>
      get {
        onSuccess(extractor ask specId) {
          case Some(currentType: ExtractorSpec) =>
            Observer("extractor_route_get_success")
            complete(
              HttpEntity(ContentTypes.`application/json`,
                         currentType.toJson.prettyPrint))
          case None =>
            Observer("extractor_route_get_notfound")
            complete(StatusCodes.NotFound)
          case e =>
            Observer("extractor_route_get_unk_err")
            logger.warn(s"unable to handle: $e")
            complete(StatusCodes.InternalServerError)
        }
      } ~
        delete {
          onSuccess(extractor ask DeleteSpec(specId)) {
            case ExtractorOk() =>
              Observer("extractor_route_delete_success")
              complete(StatusCodes.Accepted)
            case None =>
              Observer("extractor_route_get_notfound")
              complete(StatusCodes.NotFound)
            case e =>
              Observer("extractor_route_get_unk_err")
              logger.warn(s"unable to handle: $e")
              complete(StatusCodes.InternalServerError)
          }
        } ~ post {
        decodeRequest {
          entity(as[LazyExtractorSpec]) { let =>
            onSuccess(extractor ask let.spec(specId)) {
              case Some(currentType: ExtractorSpec)
                  if currentType.created == let.spec(specId).created =>
                Observer("extractor_route_post_success")
                complete(StatusCodes.Created,
                         HttpEntity(ContentTypes.`application/json`,
                                    currentType.toJson.prettyPrint))
              case Some(currentType: ExtractorSpec)
                  if currentType.created != let.spec(specId).created =>
                Observer("extractor_route_post_dupe_err")
                logger.debug(s"duplicate create request: $currentType")
                complete(StatusCodes.Conflict)
              case e =>
                Observer("extractor_route_post_unk_err")
                logger.warn(s"unable to handle: $e")
                complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    }
  }

}
