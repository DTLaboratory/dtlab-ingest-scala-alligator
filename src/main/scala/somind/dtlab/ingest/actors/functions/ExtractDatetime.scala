package somind.dtlab.ingest.actors.functions

import java.time.{ZoneOffset, ZonedDateTime}

import com.fasterxml.jackson.databind.JsonNode
import com.typesafe.scalalogging.LazyLogging
import navicore.data.navipath.dsl.NaviPathSyntax._
import somind.dtlab.ingest.models.IndexedTelemetryExtractorSpec

object ExtractDatetime extends LazyLogging {

  def apply(node: JsonNode,
            extractorSpec: IndexedTelemetryExtractorSpec): ZonedDateTime = {
    extractorSpec match {
      case _
          if extractorSpec.datetimePath.nonEmpty && extractorSpec.datetimeFmt.nonEmpty =>
        val dateFormat = new java.text.SimpleDateFormat(
          extractorSpec.datetimeFmt.get,
          java.util.Locale.US)
        dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"))
        node.query[String](extractorSpec.datetimePath.get) match {
          case Some(dateString: String) =>
            java.time.ZonedDateTime
              .ofInstant(dateFormat.parse(dateString).toInstant, ZoneOffset.UTC)
          case _ =>
            ZonedDateTime.now()
        }

      case _ =>
        ZonedDateTime.now()
    }
  }

}
