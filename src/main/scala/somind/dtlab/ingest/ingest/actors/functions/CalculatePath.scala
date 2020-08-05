package somind.dtlab.ingest.ingest.actors.functions

import com.fasterxml.jackson.databind.JsonNode
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.ingest.ingest.models._
import navicore.data.navipath.dsl.NaviPathSyntax._

object CalculatePath extends LazyLogging {

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
      node.query[String](head.path) match {
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
