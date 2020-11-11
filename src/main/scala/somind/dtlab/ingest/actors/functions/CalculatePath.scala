package somind.dtlab.ingest.actors.functions

import com.fasterxml.jackson.databind.JsonNode
import com.typesafe.scalalogging.LazyLogging
import navicore.data.navipath.dsl.NaviPathSyntax._
import somind.dtlab.ingest.models.PathSpec

object CalculatePath extends LazyLogging {

  def queryInt(node: JsonNode, path: String): Option[String] =
    node.query[Int](path).map(_.toString)

  def queryString(node: JsonNode, path: String): Option[String] =
    node.query[String](path).map(_.toLowerCase())

  def apply(node: JsonNode,
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
            queryInt(n, p)
        case _ =>
          (n: JsonNode, p: String) =>
            queryString(n, p)
      }
      f(node, head.path) match {
        case Some(instanceId) =>
          val newPath = path + "/" + head.name + "/" + instanceId
          apply(node, tail, newPath)
        case _ =>
          logger.warn(s"can not extract instanceId for ${head.name}")
          None
      }
    }
  }

}
