package somind.dtlab.ingest.actors.functions

import com.fasterxml.jackson.databind.JsonNode
import com.typesafe.scalalogging.LazyLogging
import navicore.data.navipath.dsl.NaviPathSyntax._
import somind.dtlab.ingest.models.PathSpec

object CalculatePath extends LazyLogging {

  def queryInt(node: JsonNode,
               outerNode: Option[JsonNode],
               path: String): Option[String] = {
    val r = node.query[Int](path).map(_.toString)
    if (r.isEmpty) {
      outerNode.flatMap(_.query[Int](path).map(_.toString))
    } else r
  }

  def queryString(node: JsonNode,
                  outerNode: Option[JsonNode],
                  path: String): Option[String] = {
    val r = node.query[String](path).map(_.toLowerCase())
    if (r.isEmpty) {
      outerNode.flatMap(_.query[String](path))
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
