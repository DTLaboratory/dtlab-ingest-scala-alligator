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
object TelemetryExtractorApiRoute
    extends JsonSupport
    with LazyLogging
    with Directives
    with HttpSupport {

  def apply: Route = {

    path("telemetry" / Segment) { specId =>
      get {
        onSuccess(telemetryExtractor ask specId) {
          case Some(specs: Map[String, TelemetryExtractorSpec]) =>
            Observer("telemetry_extractor_route_get_success")
            complete(
              HttpEntity(ContentTypes.`application/json`,
                         specs.toJson.prettyPrint))
          case None =>
            Observer("telemetry_extractor_route_get_notfound")
            complete(StatusCodes.NotFound)
          case e =>
            Observer("telemetry_extractor_route_get_unk_err")
            logger.warn(s"unable to handle: $e")
            complete(StatusCodes.InternalServerError)
        }
      } ~
        delete {
          onSuccess(telemetryExtractor ask DeleteSpec(specId)) {
            case ExtractorOk() =>
              Observer("telemetry_extractor_route_delete_success")
              complete(StatusCodes.Accepted)
            case None =>
              Observer("telemetry_extractor_route_get_notfound")
              complete(StatusCodes.NotFound)
            case e =>
              Observer("telemetry_extractor_route_get_unk_err")
              logger.warn(s"unable to handle: $e")
              complete(StatusCodes.InternalServerError)
          }
        } ~ post {
        decodeRequest {
          entity(as[LazyTelemetryExtractorSpec]) { let =>
            val newSpec = let.spec(specId)
            onSuccess(telemetryExtractor ask newSpec) {
              case Some(currentType: TelemetryExtractorSpec)
                  if currentType.created == newSpec.created =>
                Observer("telemetry_extractor_route_post_success")
                complete(StatusCodes.Created,
                         HttpEntity(ContentTypes.`application/json`,
                                    currentType.toJson.prettyPrint))
              case Some(currentType: TelemetryExtractorSpec)
                  if currentType.created != newSpec.created =>
                Observer("telemetry_extractor_route_post_dupe_err")
                logger.debug(s"duplicate create request: $currentType")
                complete(StatusCodes.Conflict)
              case e =>
                Observer("telemetry_extractor_route_post_unk_err")
                logger.warn(s"unable to handle: $e")
                complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    }
  }

}
