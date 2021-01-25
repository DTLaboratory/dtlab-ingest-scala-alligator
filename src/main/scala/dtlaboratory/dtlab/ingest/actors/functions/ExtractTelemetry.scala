package dtlaboratory.dtlab.ingest.actors.functions

import com.fasterxml.jackson.databind.JsonNode
import com.typesafe.scalalogging.LazyLogging
import dtlaboratory.dtlab.ingest.models._
import navicore.data.navipath.dsl.NaviPathSyntax._

object ExtractTelemetry extends LazyLogging with JsonSupport {

  def extractFromInt(path: String, node: JsonNode): Option[Double] =
    node.query[Int](path).map(_.toDouble)

  def extractFromDouble(path: String, node: JsonNode): Option[Double] =
    node.query[Double](path)

  def extractFromString(path: String, node: JsonNode): Option[Double] =
    node.query[String](path).map {
      case s if s.trim == "" =>
        logger.warn(s"empty string for value $path.  returning 0")
        0.0
      case i =>
        i.toDouble
    }

  def isAllowed(vspec: IndexedValueSpec, v: Double): Boolean = {
    v match {
      case 0.0 => vspec.extractZeros.contains(true)
      case _   => true
    }
  }

  def apply(node: JsonNode,
            outerNode: Option[JsonNode],
            extractorSpecs: Seq[IndexedTelemetryExtractorSpec])
    : Seq[(String, Telemetry)] = {
    extractorSpecs.flatMap(extractorSpec => {
      extractorSpec.values.flatMap(value => {
        logger.debug(s"extracting ${value.valueType} from ${value.path}")
        val v: Option[Double] = value.valueType match {
          case "Literal" => Some(value.path.toDouble)
          case "String"  => extractFromString(value.path, node)
          case "string"  => extractFromString(value.path, node)
          case "Int"     => extractFromInt(value.path, node)
          case "int"     => extractFromInt(value.path, node)
          case "Integer" => extractFromInt(value.path, node)
          case "integer" => extractFromInt(value.path, node)
          case "Double"  => extractFromDouble(value.path, node)
          case "double"  => extractFromDouble(value.path, node)
          case _         => extractFromString(value.path, node)
        }
        v match {
          case Some(extractedValue) if isAllowed(value, extractedValue) =>
            extractorSpec.paths.flatMap(pathSeq => {
              CalculatePath(node, outerNode, pathSeq) match {
                case Some(p) =>
                  try {
                    List(
                      (p,
                       Telemetry(value.idx,
                                 extractedValue,
                                 ExtractDatetime(node, extractorSpec))))
                  } catch {
                    case _: java.lang.ClassCastException =>
                      logger.warn(
                        s"can not extract datetime from path ${extractorSpec.datetimePath} from $node")
                      List(
                        (p,
                         Telemetry(value.idx,
                                   extractedValue,
                                   java.time.ZonedDateTime.now()))
                      )
                  }
                case _ =>
                  logger.warn(s"can not extract path from pathspec: $pathSeq")
                  List()
              }
            })
          case _ =>
            logger.debug(s"did not find ${value.path} in input $node")
            List()
        }
      })
    })
  }
}
