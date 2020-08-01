package somind.dtlab.ingest.models
import java.time.ZonedDateTime

sealed trait ExtractorResult {}
final case class ExtractorOk() extends ExtractorResult
final case class ExtractorErr(message: String) extends ExtractorResult

final case class DeleteSpec(specId: String)

final case class ValueSpec(name: String, path: String, valueType: String)

final case class ObjectExtractorSpec(
    name: String,
    path: String,
    destUrl: String,
    // datetime of creation - no updates allowed
    created: ZonedDateTime = ZonedDateTime.now()
)

// for API to avoid setting created
final case class LazyObjectExtractorSpec(
    path: String,
    destUrl: String
) {
  def spec(name: String): ObjectExtractorSpec =
    ObjectExtractorSpec(name, path, destUrl)
}

final case class ObjectExtractorSpecMap(specs: Map[String, ObjectExtractorSpec])

final case class TelemetryExtractorSpec(
    name: String,
    paths: Seq[ValueSpec],
    value: ValueSpec,
    datetimePath: Option[String],
    datetimeFmt: Option[String],
    // datetime of creation - no updates allowed
    created: ZonedDateTime = ZonedDateTime.now()
)

// for API to avoid setting created
final case class LazyTelemetryExtractorSpec(
    paths: Seq[ValueSpec],
    value: ValueSpec,
    datetimePath: Option[String],
    datetimeFmt: Option[String]
) {
  def spec(name: String): TelemetryExtractorSpec =
    TelemetryExtractorSpec(name, paths, value, datetimePath, datetimeFmt)
}

final case class TelemetryExtractorSpecMap(
    specs: Map[String, TelemetryExtractorSpec]
)
