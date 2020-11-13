package somind.dtlab.ingest.models

import java.time.ZonedDateTime

sealed trait ExtractorResult {}
final case class ExtractorOk() extends ExtractorResult
final case class ExtractorErr(message: String) extends ExtractorResult
final case class ExtractorNoData() extends ExtractorResult

final case class DeleteSpec(specId: String)

final case class PathSpec(name: String, path: String, valueType: String)
final case class ValueSpec(idx: Int, path: String, valueType: String)

final case class ObjectExtractorSpec(
    specId: String,
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
  def spec(specId: String): ObjectExtractorSpec =
    ObjectExtractorSpec(specId, path, telSpecId)
}

final case class ObjectExtractorSpecMap(specs: Map[String, ObjectExtractorSpec])

final case class TelemetryExtractorSpec(
    // id of the collection of extractors this extractor
    // belongs to - usually an ingest endpoint
    specId: String,
    // each inner sequence is a path to an actor - the outer
    // seq is the collection of paths - 1 raw input can go to
    // any number of actors
    paths: Seq[Seq[PathSpec]],
    // the value name will be the actor property name, value
    // the actor value
    values: Seq[ValueSpec],
    datetimePath: Option[String] = None,
    datetimeFmt: Option[String] = None,
    // datetime of creation - no updates allowed
    created: ZonedDateTime = ZonedDateTime.now()
)

// for API to avoid setting created
final case class LazyTelemetryExtractorSpec(
    paths: Seq[Seq[PathSpec]],
    values: Seq[ValueSpec],
    datetimePath: Option[String] = None,
    datetimeFmt: Option[String] = None
) {
  def spec(specId: String): TelemetryExtractorSpec =
    TelemetryExtractorSpec(specId, paths, values, datetimePath, datetimeFmt)
}

// outer key is specId, inner key is valueName
final case class TelemetryExtractorSpecMap(
    specs: Map[String, Seq[TelemetryExtractorSpec]]
)

final case class Specs(specs: Seq[TelemetryExtractorSpec])

final case class Telemetry(
    idx: Int,
    value: Double,
    datetime: ZonedDateTime = ZonedDateTime.now()
)
