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
  * Extractor API lets you define how to turn raw json input into DtLab-ready telemetry messages.
  * The "object" extractor expects to have to pull an array of collections of observations from
  * the raw input.  An example would be a fleet controller sending batches of odometer and speed
  * telemetry on behalf of multiple vehicles in a single json message.
  *
  *  TODO: combine the object extractor and telemetry exatractor routes and implementations.
  *  The route need not be different - the spec can imply to repost array contents.
  */
object ObjectExtractorApiRoute
    extends JsonSupport
    with LazyLogging
    with Directives
    with HttpSupport {

  def apply: Route = {
    path("object" / Segment) { specId =>
      get {
        onSuccess(objectExtractor ask specId) {
          case Some(currentType: ObjectExtractorSpec) =>
            Observer("object_extractor_route_get_success")
            complete(
              HttpEntity(ContentType(MediaTypes.`application/json`),
                         currentType.toJson.prettyPrint))
          case None =>
            Observer("object_extractor_route_get_notfound")
            complete(StatusCodes.NotFound)
          case e =>
            Observer("object_extractor_route_get_unk_err")
            logger.warn(s"unable to handle: $e")
            complete(StatusCodes.InternalServerError)
        }
      } ~
        delete {
          onSuccess(objectExtractor ask DeleteSpec(specId)) {
            case ExtractorOk() =>
              Observer("object_extractor_route_delete_success")
              complete(StatusCodes.Accepted)
            case None =>
              Observer("object_extractor_route_get_notfound")
              complete(StatusCodes.NotFound)
            case e =>
              Observer("object_extractor_route_get_unk_err")
              logger.warn(s"unable to handle: $e")
              complete(StatusCodes.InternalServerError)
          }
        } ~ post {
        decodeRequest {
          entity(as[LazyObjectExtractorSpec]) { let =>
            val newSpec = let.spec(specId)
            onSuccess(objectExtractor ask newSpec) {
              case Some(currentType: ObjectExtractorSpec)
                  if currentType.created == newSpec.created =>
                Observer("object_extractor_route_post_success")
                complete(
                  HttpResponse(
                    StatusCodes.Created,
                    entity =
                      HttpEntity(ContentType(MediaTypes.`application/json`),
                                 currentType.toJson.prettyPrint)
                  )
                )
              case Some(currentType: ObjectExtractorSpec)
                  if currentType.created != newSpec.created =>
                Observer("object_extractor_route_post_dupe_err")
                logger.debug(s"duplicate create request: $currentType")
                complete(StatusCodes.Conflict)
              case e =>
                Observer("object_extractor_route_post_unk_err")
                logger.warn(s"unable to handle: $e")
                complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    }
  }

}
