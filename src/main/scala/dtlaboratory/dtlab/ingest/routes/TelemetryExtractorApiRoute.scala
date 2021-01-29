package dtlaboratory.dtlab.ingest.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.pattern.ask
import com.typesafe.scalalogging.LazyLogging
import dtlaboratory.dtlab.ingest.models._
import dtlaboratory.dtlab.ingest.observe.Observer
import dtlaboratory.dtlab.ingest.Conf._
import spray.json._

/**
  * Extractor API lets you define how to turn raw json input into DtLab telemetry messages.
  *
  * This implementation expects no arrays, just unique observations.  IE: one device is reported.
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
          case Some(specs: IndexedSpecs @unchecked) =>
            Observer("telemetry_extractor_route_get_success")
            complete(
              HttpResponse(
                StatusCodes.OK,
                entity = HttpEntity(ContentTypes.`application/json`,
                                    specs.specs.toJson.prettyPrint)
              )
            )
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
            val newSpecs: Seq[NamedTelemetryExtractorSpec] =
              lazySpecs.map(_.spec(specId))
            onSuccess(telemetryExtractor ask NamedSpecs(newSpecs)) {
              case Some(specs: IndexedSpecs @unchecked)
                  if specs.specs.head.created == newSpecs.head.created =>
                Observer("telemetry_extractor_route_post_success")
                complete(StatusCodes.Created,
                         HttpEntity(ContentType(MediaTypes.`application/json`),
                                    specs.specs.toJson.prettyPrint))
              case Some(specs: IndexedSpecs @unchecked)
                  if specs.specs.head.created != newSpecs.head.created =>
                Observer("telemetry_extractor_route_post_dupe_err")
                complete(StatusCodes.Conflict)
              case ExtractorErr(emsg) =>
                Observer("telemetry_extractor_route_post_type_err")
                logger.warn(s"unable to handle due to type error: $emsg")
                complete(StatusCodes.BadRequest, emsg)
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
