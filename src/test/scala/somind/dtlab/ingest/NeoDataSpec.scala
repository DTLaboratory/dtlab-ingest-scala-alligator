package somind.dtlab.ingest

import java.io.InputStream

import com.fasterxml.jackson.databind.JsonNode
import navicore.data.navipath.dsl.NaviPathSyntax._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import somind.dtlab.ingest.ingest.actors.functions.CalculatePath
import somind.dtlab.ingest.ingest.models.{TelemetryExtractorSpec, ValueSpec}

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
          ValueSpec("orbiting_body",
                    "$.close_approach_data[0].orbiting_body",
                    "String"),
          ValueSpec("object", "$.neo_reference_id", "String")
        )),
      ValueSpec("min",
                "$.estimated_diameter.meters.estimated_diameter_min",
                "Double")
    )

    val p = CalculatePath(obj.get, eSpec.paths.head)

    p should contain("/orbiting_body/Earth/object/3726710")

  }

}
