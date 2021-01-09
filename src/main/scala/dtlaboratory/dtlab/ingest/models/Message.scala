package dtlaboratory.dtlab.ingest.models

import java.time.ZonedDateTime

sealed trait ExtractorResult {}
final case class ExtractorOk() extends ExtractorResult
final case class ExtractorErr(message: String) extends ExtractorResult
final case class ExtractorNoData() extends ExtractorResult

final case class DeleteSpec(specId: String)

final case class PathSpec(name: String, path: String, valueType: String)
final case class IndexedValueSpec(idx: Int,
                                  path: String,
                                  valueType: String,
                                  extractZeros: Option[Boolean] = None)
final case class NamedValueSpec(name: String,
                                typeId: String,
                                path: String,
                                valueType: String,
                                extractZeros: Option[Boolean] = None)

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

abstract class TelemetryExtractorSpec {
  // id of the collection of extractors this extractor
  // belongs to - usually an ingest endpoint
  def specId: String
  // each inner sequence is a path to an actor - the outer
  // seq is the collection of paths - 1 raw input can go to
  // any number of actors
  def paths: Seq[Seq[PathSpec]]
  // the value name will be the actor property name, value
  // the actor value
  def datetimePath: Option[String]
  def datetimeFmt: Option[String]
  // datetime of creation - no updates allowed
  def created: ZonedDateTime
}

final case class NamedTelemetryExtractorSpec(
    specId: String,
    paths: Seq[Seq[PathSpec]],
    datetimePath: Option[String] = None,
    datetimeFmt: Option[String] = None,
    created: ZonedDateTime = ZonedDateTime.now(),
    values: Seq[NamedValueSpec] // the actor value
) extends TelemetryExtractorSpec

final case class IndexedTelemetryExtractorSpec(
    specId: String,
    paths: Seq[Seq[PathSpec]],
    // the actor value
    values: Seq[IndexedValueSpec],
    datetimePath: Option[String] = None,
    datetimeFmt: Option[String] = None,
    created: ZonedDateTime = ZonedDateTime.now()
) extends TelemetryExtractorSpec

// for API to avoid setting created
final case class LazyTelemetryExtractorSpec(
    paths: Seq[Seq[PathSpec]],
    values: Seq[NamedValueSpec],
    datetimePath: Option[String] = None,
    datetimeFmt: Option[String] = None
) {
  def spec(specId: String): NamedTelemetryExtractorSpec =
    NamedTelemetryExtractorSpec(specId,
                                paths,
                                datetimePath,
                                datetimeFmt,
                                created = ZonedDateTime.now(),
                                values)
}

// outer key is specId, inner key is valueName
final case class TelemetryExtractorSpecMap(
    specs: Map[String, Seq[IndexedTelemetryExtractorSpec]]
)

final case class NamedSpecs(specs: Seq[NamedTelemetryExtractorSpec])
final case class IndexedSpecs(specs: Seq[IndexedTelemetryExtractorSpec])

final case class Telemetry(
    idx: Int,
    value: Double,
    datetime: ZonedDateTime = ZonedDateTime.now()
)

final case class DtType(
    name: String,
    props: Option[Seq[String]],
    children: Option[Set[String]],
    created: ZonedDateTime = ZonedDateTime.now()
)
