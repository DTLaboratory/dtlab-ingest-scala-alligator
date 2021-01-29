package dtlaboratory.dtlab

import java.io.InputStream
import com.fasterxml.jackson.databind.JsonNode
import dtlaboratory.dtlab.ingest.actors.functions._
import dtlaboratory.dtlab.ingest.actors.functions.jsonextractor.{CalculatePathFromJson, ExtractJsonTelemetry}
import dtlaboratory.dtlab.ingest.models._
import navicore.data.navipath.dsl.NaviPathSyntax._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.io.Source

class DatetimeNeoDataSpec extends AnyFlatSpec with Matchers {

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

    val eSpec = IndexedTelemetryExtractorSpec(
      "neo1",
      Seq(
        Seq(
          PathSpec("orbiting_body",
                    "$.close_approach_data[0].orbiting_body",
                    "String"),
          PathSpec("object", "$.neo_reference_id", "String")
        )),
      Seq(
        IndexedValueSpec(0,
                  "$.estimated_diameter.meters.estimated_diameter_min",
                  "Double"),
        IndexedValueSpec(1,
                  "$.estimated_diameter.meters.estimated_diameter_max",
                  "Double")
      ),
      Some("$.close_approach_data[0].close_approach_date_full"),
      Some("yyyy-MMM-dd hh:mm")
    )

    val p = CalculatePathFromJson(obj.get, None, eSpec.paths.head)

    p should contain("/orbiting_body/earth/object/3726710")

    val r = ExtractJsonTelemetry(obj.get, None, Seq(eSpec))

    r.nonEmpty should be(true)

    println(r.head._1)
    println(r.head._2)

    r.head._2.datetime.getYear should be (2015)

  }

}
