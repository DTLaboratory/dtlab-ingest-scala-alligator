package dtlaboratory.dtlab

import java.io.InputStream

import org.scalatest._
import matchers._

import scala.io.Source
import navicore.data.navipath.dsl.NaviPathSyntax._
import org.scalatest.flatspec.AnyFlatSpec

class JsonPathQuerySpec extends AnyFlatSpec with should.Matchers {
  val stream: InputStream = getClass.getResourceAsStream("/childquery.json")
  val jsonString: String = Source.fromInputStream(stream).mkString

  "A value" should "match" in {

    val parsedJson = jsonString.asJson

    val r = parsedJson.query[Int]("$.weight.average[?(@.units==\"lbs\")].value")
    r should be (Some(927))

    val nf = parsedJson.query[Int]("$.weight.average[?(@.units==\"grams\")].value")
    nf should not be 'defined

  }

}
