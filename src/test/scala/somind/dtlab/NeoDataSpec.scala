package somind.dtlab

import java.io.InputStream

import com.fasterxml.jackson.databind.JsonNode
import navicore.data.navipath.dsl.NaviPathSyntax._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import somind.dtlab.ingest.actors.functions.CalculatePath
import somind.dtlab.ingest.models._

import scala.io.Source

class NeoDataSpec extends AnyFlatSpec with Matchers {

  val stream: InputStream =
    getClass.getResourceAsStream("/near_earth_objects.json")
  val jsonString: String = Source.fromInputStream(stream).mkString

  "An obj" should "handle list objects query" in {

    val results: Option[List[JsonNode]] =
      jsonString.query[List[JsonNode]]("$.near_earth_objects.*[*]")

    assert(results.nonEmpty)
    assert(results.get.size == 24)

    val obj = results.get.headOption

    obj should be('defined)

    val eSpec = TelemetryExtractorSpec(
      "neo1",
      Seq(
        Seq(
          PathSpec("orbiting_body",
                    "$.close_approach_data[0].orbiting_body",
                    "String"),
          PathSpec("object", "$.neo_reference_id", "String")
        )),
      Seq(
        ValueSpec(0,
                  "$.estimated_diameter.meters.estimated_diameter_min",
                  "Double"),
        ValueSpec(1,
                  "$.estimated_diameter.meters.estimated_diameter_max",
                  "Double")
      )
    )

    val p = CalculatePath(obj.get, eSpec.paths.head)

    p should contain("/orbiting_body/earth/object/3726710")

  }

}
