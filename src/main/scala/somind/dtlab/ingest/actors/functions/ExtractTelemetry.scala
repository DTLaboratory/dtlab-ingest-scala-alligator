package somind.dtlab.ingest.actors.functions

import java.time.ZonedDateTime

import com.fasterxml.jackson.databind.JsonNode
import com.typesafe.scalalogging.LazyLogging
import navicore.data.navipath.dsl.NaviPathSyntax._
import somind.dtlab.ingest.models.{Telemetry, TelemetryExtractorSpec}

object ExtractTelemetry extends LazyLogging {

  def apply(
      node: JsonNode,
      extractorSpecs: Seq[TelemetryExtractorSpec]): Seq[(String, Telemetry)] = {
    extractorSpecs.flatMap(extractorSpec => {
      extractorSpec.values.flatMap(value => {
        node.query[Double](value.path) match {
          case Some(extractedValue) =>
            extractorSpec.paths.flatMap(pathSeq => {
              CalculatePath(node, pathSeq) match {
                case Some(p) =>
                  try {
                    List(
                      (p,
                       Telemetry(value.idx,
                                 extractedValue,
                                 ExtractDatetime(node, extractorSpec))))
                  } catch {
                    case e: Throwable =>
                      logger.warn(
                        s"can not extract datetime $value: ${e.getMessage}")
                      List(
                        (p,
                         Telemetry(value.idx,
                                   extractedValue,
                                   ZonedDateTime.now()))
                      )
                  }
                case _ =>
                  logger.warn(s"can not extract path from pathspec: $pathSeq")
                  List()
              }
            })
          case _ =>
            logger.debug(s"did not find ${value.path} in input")
            List()
        }
      })
    })
  }
}
