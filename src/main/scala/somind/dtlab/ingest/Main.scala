package somind.dtlab.ingest

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.ingest.Conf._
import somind.dtlab.ingest.models.JsonSupport
import somind.dtlab.ingest.observe.ObserverRoute
import somind.dtlab.ingest.routes._

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
                        ArrayIngestRoute.apply
                    }
                  }
                }
              }
            }
          }
        }

    Http().bindAndHandle(route, "0.0.0.0", port)
  }
}
