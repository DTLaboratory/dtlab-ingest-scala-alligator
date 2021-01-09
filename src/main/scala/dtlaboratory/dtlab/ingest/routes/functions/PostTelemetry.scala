package dtlaboratory.dtlab.ingest.routes.functions

import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model._
import akka.http.scaladsl.{Http, HttpExt}
import dtlaboratory.dtlab.ingest.Conf._
import dtlaboratory.dtlab.ingest.models.{JsonSupport, Telemetry}
import dtlaboratory.dtlab.ingest.routes.HttpSupport
import spray.json._

import scala.concurrent.Future

object PostTelemetry extends JsonSupport with HttpSupport {

  val http: HttpExt = Http(system)

  def applyPost(path: String, telem: Telemetry): Future[HttpResponse] = {
    val request = HttpRequest()
    val newUri =
      request.uri
        .withScheme(dtlabScheme)
        .withHost(dtlabHost)
        .withPort(dtlabPort)
        .withPath(Path("/" + urlpath + "/actor" + path))
    val newRequest = HttpRequest(
      method = HttpMethods.POST,
      uri = newUri,
      entity =
        HttpEntity(ContentTypes.`application/json`, telem.toJson.compactPrint))
    logger.debug(s"sending request to: " + newRequest)
    http
      .singleRequest(newRequest)
      .map(s => {
        logger.debug(s"applyPost code: ${s.status}")
        s
      })
  }

  // does this make sense?  is this how bad scala really is????
  // ensure that futures are executed 1 at a time
  // we are not so concerned about the latency for a single batch - system scales horizontally when there
  // are lots of writers/posters.
  def seqFutures[T, U](items: TraversableOnce[T])(
      yourfunction: T => Future[U]): Future[List[U]] = {
    items.foldLeft(Future.successful[List[U]](Nil)) { (f, item) =>
      f.flatMap { x =>
        yourfunction(item).map(_ :: x)
      }
    } map (_.reverse)
  }

  def apply(telemetry: Seq[(String, Telemetry)]): Future[Seq[HttpResponse]] = {

    seqFutures[(String, Telemetry), HttpResponse](telemetry)(
      (i: (String, Telemetry)) => {
        applyPost(i._1, i._2)
      })

  }

}
