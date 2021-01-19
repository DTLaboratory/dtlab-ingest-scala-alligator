package dtlaboratory.dtlab.ingest.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

import java.time.{ZoneOffset, ZonedDateTime}
import java.util.{Date, UUID}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  val defaultDateFmtStr = "yyyy-MM-dd'T'HH:mm:ss.SSSX"
  val dateFormat =
    new java.text.SimpleDateFormat(defaultDateFmtStr, java.util.Locale.US)
  dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"))

  def parse8601(dateString: String): java.util.Date =
    dateFormat.parse(dateString)

  def get8601(date: java.util.Date): String =
    dateFormat.format(date)

  def now8601(): String = {
    val now = new java.util.Date()
    get8601(now)
  }

  implicit object Date extends JsonFormat[Date] {
    def write(dt: java.util.Date): JsValue = JsString(get8601(dt))
    def read(value: JsValue): java.util.Date = {
      value match {
        case JsString(dt) => parse8601(dt)
        case _            => throw DeserializationException("Expected 8601")
      }
    }
  }

  implicit object UUIDFormat extends JsonFormat[UUID] {
    def write(uuid: UUID): JsValue = JsString(uuid.toString)
    def read(value: JsValue): UUID = {
      value match {
        case JsString(uuid) => UUID.fromString(uuid)
        case _ =>
          throw DeserializationException("Expected hexadecimal UUID string")
      }
    }
  }

  implicit object ZonedDateTime extends JsonFormat[ZonedDateTime] {
    def write(dt: ZonedDateTime): JsValue =
      JsString(get8601(new Date(dt.toInstant.toEpochMilli))) // ugh.  replace SimpleDateFormat with new java.time.* stuff
    def read(value: JsValue): ZonedDateTime = {
      value match {
        case JsString(dt) =>
          java.time.ZonedDateTime
            .ofInstant(parse8601(dt).toInstant, ZoneOffset.UTC)
        case _ => throw DeserializationException("Expected 8601")
      }
    }
  }

  implicit val vsFmt: RootJsonFormat[IndexedValueSpec] = jsonFormat4(
    IndexedValueSpec)
  implicit val nvsFmt: RootJsonFormat[NamedValueSpec] = jsonFormat5(
    NamedValueSpec)
  implicit val psFmt: RootJsonFormat[PathSpec] = jsonFormat3(PathSpec)
  implicit val ltesFmt: RootJsonFormat[LazyTelemetryExtractorSpec] =
    jsonFormat4(LazyTelemetryExtractorSpec)
  implicit val tesFmt: RootJsonFormat[IndexedTelemetryExtractorSpec] =
    jsonFormat6(IndexedTelemetryExtractorSpec)
  implicit val ntesFmt: RootJsonFormat[NamedTelemetryExtractorSpec] =
    jsonFormat6(NamedTelemetryExtractorSpec)
  implicit val tesmFmt: RootJsonFormat[TelemetryExtractorSpecMap] = jsonFormat1(
    TelemetryExtractorSpecMap)
  implicit val loesFmt: RootJsonFormat[LazyObjectExtractorSpec] = jsonFormat2(
    LazyObjectExtractorSpec)
  implicit val oesFmt: RootJsonFormat[ObjectExtractorSpec] = jsonFormat4(
    ObjectExtractorSpec)
  implicit val oesmFmt: RootJsonFormat[ObjectExtractorSpecMap] = jsonFormat1(
    ObjectExtractorSpecMap)
  implicit val specsFmt: RootJsonFormat[IndexedSpecs] = jsonFormat1(
    IndexedSpecs)
  implicit val nspecsFmt: RootJsonFormat[NamedSpecs] = jsonFormat1(NamedSpecs)
  implicit val telFmt: RootJsonFormat[Telemetry] = jsonFormat3(Telemetry)

  implicit val dttype: RootJsonFormat[DtType] = jsonFormat4(DtType)

}
