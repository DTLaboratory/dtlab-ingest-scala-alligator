package dtlaboratory.dtlab.ingest.actors.functions.jsonextractor

import com.fasterxml.jackson.databind.JsonNode
import com.typesafe.scalalogging.LazyLogging
import dtlaboratory.dtlab.ingest.models.PathSpec
import navicore.data.navipath.dsl.NaviPathSyntax._

object CalculatePathFromJson extends LazyLogging {

  def queryInt(node: JsonNode,
               outerNode: Option[JsonNode],
               path: String): Option[String] = {
    if (path.head != '$') return Some(path) // literal id
    val r = node.query[Int](path).map(_.toString)
    if (r.isEmpty) {
      outerNode.flatMap(_.query[Int](path).map(_.toString))
    } else r
  }

  def safeId(id: String): String = id.toLowerCase().replace(' ', '_')

  def queryString(node: JsonNode,
                  outerNode: Option[JsonNode],
                  path: String): Option[String] = {
    if (path.head != '$') return Some(path) // literal id
    val r = node.query[String](path).map(safeId)
    if (r.isEmpty) {
      outerNode.flatMap(_.query[String](path).map(safeId))
    } else r
  }

  def apply(node: JsonNode,
            outerNode: Option[JsonNode],
            valueSpecs: Seq[PathSpec],
            path: String = ""): Option[String] = {
    if (valueSpecs.isEmpty) {
      if (path == "")
        None
      else
        Some(path)
    } else {
      val head :: tail = valueSpecs
      val vType = head.valueType
      val f = vType match {
        case "Int" =>
          (n: JsonNode, p: String) =>
            queryInt(n, outerNode, p)
        case _ =>
          (n: JsonNode, p: String) =>
            queryString(n, outerNode, p)
      }
      f(node, head.path) match {
        case Some(instanceId) =>
          val newPath = path + "/" + head.name + "/" + instanceId
          apply(node, outerNode, tail, newPath)
        case _ =>
          logger.warn(s"can not extract instanceId for ${head.name}")
          None
      }
    }
  }

}
