package somind.dtlab.ingest.models
import java.time.ZonedDateTime

sealed trait ExtractorResult {}
final case class ExtractorOk() extends ExtractorResult
final case class ExtractorErr(message: String) extends ExtractorResult

final case class DeleteSpec(specId: String)

final case class ValueSpec(name: String, path: String, valueType: String)

final case class ExtractorSpec(
    name: String,
    paths: Seq[ValueSpec],
    value: ValueSpec,
    datetimePath: Option[String],
    datetimeFmt: Option[String],
    // datetime of creation - no updates allowed
    created: ZonedDateTime = ZonedDateTime.now()
)

// for API to avoid setting created
final case class LazyExtractorSpec(
    paths: Seq[ValueSpec],
    value: ValueSpec,
    datetimePath: Option[String],
    datetimeFmt: Option[String],
    // datetime of creation - no updates allowed
    created: Option[ZonedDateTime]
) {
  def spec(name: String): ExtractorSpec =
    ExtractorSpec(name,
                  paths,
                  value,
                  datetimePath,
                  datetimeFmt,
                  created.getOrElse(ZonedDateTime.now()))
}

final case class ExtractorSpecMap(
    specs: Map[String, ExtractorSpec]
)
