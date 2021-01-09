package dtlaboratory.dtlab.ingest.routes.functions

import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.{Http, HttpExt}
import dtlaboratory.dtlab.ingest.models.{DtType, JsonSupport}
import dtlaboratory.dtlab.ingest.routes.HttpSupport
import dtlaboratory.dtlab.ingest.Conf._

import scala.concurrent.{Await, Future}

object GetDtType extends JsonSupport with HttpSupport {

  val http: HttpExt = Http(system)
  val request: HttpRequest = HttpRequest().withMethod(HttpMethods.GET)

  def apply(typeId: String): Option[DtType] = {

    import scala.concurrent.duration._
    val f = GetDtType.applyAsync(typeId)
    val r: HttpResponse = Await.result(f, 5.seconds)
    if (!r.status.isSuccess()) {
      logger.warn(s"type lookup not successful: $r")
      return None
    }
    val uf: Future[DtType] = Unmarshal(r).to[DtType]
    Some(Await.result(uf, 5.seconds))
  }

  def applyAsync(typeId: String): Future[HttpResponse] = {
    val newRequest =
      request
        .withUri(
          uri = Uri(s"http://$dtlabHost:$dtlabPort/$dtlabPath/type/$typeId"))
    http.singleRequest(newRequest)
  }

}
