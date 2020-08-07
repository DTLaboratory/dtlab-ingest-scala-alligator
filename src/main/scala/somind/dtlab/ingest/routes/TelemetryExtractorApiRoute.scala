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
          case Some(specs: Specs @unchecked) =>
            Observer("telemetry_extractor_route_get_success")
            complete(
              HttpEntity(ContentTypes.`application/json`,
                         specs.specs.toJson.prettyPrint))
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
          entity(as[Seq[LazyTelemetryExtractorSpec]]) { lazySpecs =>
            val newSpecs = lazySpecs.map(_.spec(specId))
            onSuccess(telemetryExtractor ask Specs(newSpecs)) {
              case Some(specs: Specs @unchecked)
                  if specs.specs.head.created == newSpecs.head.created =>
                Observer("telemetry_extractor_route_post_success")
                complete(StatusCodes.Created,
                         HttpEntity(ContentTypes.`application/json`,
                                    specs.specs.toJson.prettyPrint))
              case Some(specs: Specs @unchecked)
                  if specs.specs.head.created != newSpecs.head.created =>
                Observer("telemetry_extractor_route_post_dupe_err")
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
