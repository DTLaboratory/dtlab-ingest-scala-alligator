package somind.dtlab.ingest.ingest.routes.functions

import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model._
import akka.http.scaladsl.{Http, HttpExt}
import somind.dtlab.ingest.ingest.Conf._
import somind.dtlab.ingest.ingest.models.{JsonSupport, Telemetry}
import somind.dtlab.ingest.ingest.routes.HttpSupport
import spray.json._

import scala.concurrent.Future

object PostTelemetry extends JsonSupport with HttpSupport {

  val http: HttpExt = Http(system)

  def applyPost(request: HttpRequest,
                path: String,
                telem: Telemetry): Future[HttpResponse] = {
    val newUri =
      request.uri
        .withHost(dtlabHost)
        .withPort(dtlabPort)
        .withPath(Path("/" + urlpath + "/actor" + path))
    logger.debug(s"sending telemetry to: " + newUri)
    val newRequest = request.copy(
      uri = newUri,
      entity =
        HttpEntity(ContentTypes.`application/json`, telem.toJson.compactPrint))
    logger.debug(s"sending request to: " + newRequest)
    http.singleRequest(newRequest)
  }

  // ejs does this make sense?  is this how bad scala really is????
  // ejs does this make sense?  is this how bad scala really is????
  // ejs does this make sense?  is this how bad scala really is????
  // ejs does this make sense?  is this how bad scala really is????
  // ejs does this make sense?  is this how bad scala really is????
  def seqFutures[T, U](items: TraversableOnce[T])(
      yourfunction: T => Future[U]): Future[List[U]] = {
    items.foldLeft(Future.successful[List[U]](Nil)) { (f, item) =>
      f.flatMap { x =>
        yourfunction(item).map(_ :: x)
      }
    } map (_.reverse)
  }

  def apply(request: HttpRequest,
            telemetry: Seq[(String, Telemetry)]): Future[Seq[HttpResponse]] = {

    seqFutures[(String, Telemetry), HttpResponse](telemetry)(
      (i: (String, Telemetry)) => {
        applyPost(request, i._1, i._2)
      })

//    Future.traverse(telemetry)(i => {
//      applyPost(request, i._1, i._2)
//    })

  }

}
