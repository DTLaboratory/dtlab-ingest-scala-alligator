package somind.dtlab.ingest.ingest.models

import java.time.ZonedDateTime

sealed trait ExtractorResult {}
final case class ExtractorOk() extends ExtractorResult
final case class ExtractorErr(message: String) extends ExtractorResult

final case class DeleteSpec(specId: String)

final case class ValueSpec(name: String, path: String, valueType: String)

final case class ObjectExtractorSpec(
    name: String,
    path: String,
    telSpecId: String,
    // datetime of creation - no updates allowed
    created: ZonedDateTime = ZonedDateTime.now()
)

// for API to avoid setting created
final case class LazyObjectExtractorSpec(
    path: String,
    telSpecId: String,
) {
  def spec(name: String): ObjectExtractorSpec =
    ObjectExtractorSpec(name, path, telSpecId)
}

final case class ObjectExtractorSpecMap(specs: Map[String, ObjectExtractorSpec])

final case class TelemetryExtractorSpec(
    name: String,
    paths: Seq[ValueSpec],
    value: ValueSpec,
    datetimePath: Option[String] = None,
    datetimeFmt: Option[String] = None,
    // datetime of creation - no updates allowed
    created: ZonedDateTime = ZonedDateTime.now()
)

// for API to avoid setting created
final case class LazyTelemetryExtractorSpec(
    paths: Seq[ValueSpec],
    value: ValueSpec,
    datetimePath: Option[String] = None,
    datetimeFmt: Option[String] = None
) {
  def spec(name: String): TelemetryExtractorSpec =
    TelemetryExtractorSpec(name, paths, value, datetimePath, datetimeFmt)
}

// outer key is specId, inner key is valueName
final case class TelemetryExtractorSpecMap(
    specs: Map[String, Map[String, TelemetryExtractorSpec]]
)
