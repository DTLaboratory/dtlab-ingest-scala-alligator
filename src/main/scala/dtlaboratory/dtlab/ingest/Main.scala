package dtlaboratory.dtlab.ingest

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import com.typesafe.scalalogging.LazyLogging
import dtlaboratory.dtlab.ingest.models.JsonSupport
import dtlaboratory.dtlab.ingest.observe.ObserverRoute
import dtlaboratory.dtlab.ingest.routes._
import Conf._

object Main extends LazyLogging with JsonSupport with HttpSupport {

  def main(args: Array[String]) {

    val route =
      ObserverRoute.apply ~
        handleErrors {
          cors(corsSettings) {
            handleErrors {
              logRequest(urlpath) {
                pathPrefix(urlpath) {
                  ignoreTrailingSlash {
                    pathPrefix("extractor") {
                      TelemetryExtractorApiRoute.apply ~
                        ObjectExtractorApiRoute.apply
                    } ~
                      pathPrefix("ingest") {
                        IngestRoute.apply("array", objectExtractor) ~
                          IngestRoute.apply("telemetry", telemetryExtractor)
                      }
                  }
                }
              }
            }
          }
        }

    Http().newServerAt("0.0.0.0", port).bindFlow(route)
  }
}
