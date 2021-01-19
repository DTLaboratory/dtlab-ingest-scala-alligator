package dtlaboratory.dtlab.ingest.actors.functions

import com.fasterxml.jackson.databind.JsonNode
import com.typesafe.scalalogging.LazyLogging
import dtlaboratory.dtlab.ingest.models.IndexedTelemetryExtractorSpec
import navicore.data.navipath.dsl.NaviPathSyntax._

import java.time.{ZoneOffset, ZonedDateTime}
import java.util.Date

object ExtractDatetime extends LazyLogging {

  def apply(node: JsonNode,
            extractorSpec: IndexedTelemetryExtractorSpec): ZonedDateTime = {

    if (extractorSpec.datetimePath.isEmpty) {
      java.time.ZonedDateTime.now()
    } else {
      extractorSpec.datetimeFmt match {
        case Some("epoch_string") =>
          node.query[String](extractorSpec.datetimePath.get) match {
            case Some(number: String) =>
              logger.debug(s"long epoch string used to format date")
              ZonedDateTime.ofInstant(new Date(number.toLong).toInstant,
                                      ZoneOffset.UTC)
            case _ =>
              logger.warn(
                s"bad date format - using current time: $extractorSpec")
              ZonedDateTime.now()
          }
        case Some("epoch_long") =>
          node.query[Long](extractorSpec.datetimePath.get) match {
            case Some(number: Long) =>
              logger.debug(s"long epoch number used to format date")
              ZonedDateTime.ofInstant(new Date(number).toInstant,
                                      ZoneOffset.UTC)
            case _ =>
              logger.warn(
                s"bad date format - using current time: $extractorSpec")
              ZonedDateTime.now()
          }
        case Some(fmtStr) =>
          val fmt = new java.text.SimpleDateFormat(fmtStr, java.util.Locale.US)
          fmt.setTimeZone(java.util.TimeZone.getTimeZone("UTC"))
          node.query[String](extractorSpec.datetimePath.get) match {
            case Some(dateString: String) =>
              java.time.ZonedDateTime
                .ofInstant(fmt.parse(dateString).toInstant, ZoneOffset.UTC)
            case _ =>
              ZonedDateTime.now()
          }
        case _ =>
          logger.warn(s"no date format - using current time: $extractorSpec")
          ZonedDateTime.now()
      }

    }

  }

}
