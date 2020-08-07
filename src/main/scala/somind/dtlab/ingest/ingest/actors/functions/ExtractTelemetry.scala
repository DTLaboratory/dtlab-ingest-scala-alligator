package somind.dtlab.ingest.ingest.actors.functions

import java.time.ZonedDateTime

import com.fasterxml.jackson.databind.JsonNode
import com.typesafe.scalalogging.LazyLogging
import navicore.data.navipath.dsl.NaviPathSyntax._
import somind.dtlab.ingest.ingest.models._
import somind.dtlab.ingest.ingest.observe.Observer

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
                    // ejs refactor this mess .... catching throwable, deep nesting, etc... not cool
                    val r = List(
                      (p,
                       Telemetry(value.idx,
                                 extractedValue,
                                 ExtractDatetime(node, extractorSpec))))
                    Observer("extract_telemetry_datetime_succeeded")
                    r
                  } catch {
                    case e: Throwable =>
                      Observer("extract_telemetry_datetime_failed")
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
