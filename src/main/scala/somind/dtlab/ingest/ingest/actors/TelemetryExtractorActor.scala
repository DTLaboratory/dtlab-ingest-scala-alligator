package somind.dtlab.ingest.ingest.actors

import akka.persistence._
import com.fasterxml.jackson.databind.JsonNode
import com.typesafe.scalalogging.LazyLogging
import navicore.data.navipath.dsl.NaviPathSyntax._
import somind.dtlab.ingest.ingest.actors.functions.CalculatePath
import somind.dtlab.ingest.ingest.models.{DeleteSpec, ExtractorOk, TelemetryExtractorSpec, TelemetryExtractorSpecMap}
import somind.dtlab.ingest.ingest.observe.Observer

object TelemetryExtractorActor extends LazyLogging {
  def name: String = this.getClass.getName
}

class TelemetryExtractorActor
    extends PersistentActorBase[TelemetryExtractorSpecMap]
    with LazyLogging {

  override var state: TelemetryExtractorSpecMap = TelemetryExtractorSpecMap(
    specs = Map())

  override def receiveCommand: Receive = {

    // extract telemetry from raw json
    case (specId: String, node: JsonNode) =>
      Observer("telemetry_extractor_object_request")
      state.specs.get(specId) match {
        case Some(extractorSpec) =>
          extractorSpec.values.foreach(value => {
            // todo: inspect type in spec and convert String, Int, Long to Double
            node.query[Double](value.path) match {
              case Some(extractedValue) =>
                extractorSpec.paths.foreach(pathSeq => {
                  val p = CalculatePath(node, pathSeq)
                  logger.debug(
                    s"extracting telemetry ${value.name} $extractedValue to actor path: $p")
                })
              case _ =>
                logger.debug(s"did not find ${value.name} in input")
            }
          })
        case _ =>
          logger.warn(s"did not find $specId for JsonNode extract.")
      }

    // manage specs
    case spec: TelemetryExtractorSpec =>
      state.specs.get(spec.specId) match {
        case Some(prev) =>
          logger.debug(s"create found existing ${spec.specId}")
          sender ! Some(prev)
          Observer("telemetry_extractor_spec_create_conflict")
        case _ =>
          logger.debug(s"did not find specs for ${spec.specId}.  creating...")
          state = TelemetryExtractorSpecMap(state.specs + (spec.specId -> spec))
          Observer("telemetry_extractor_spec_created")
          persistAsync(spec) { _ =>
            sender ! Some(spec)
            takeSnapshot()
          }
      }

    case del: DeleteSpec =>
      state.specs.get(del.specId) match {
        case Some(_) =>
          state = TelemetryExtractorSpecMap(state.specs - del.specId)
          persistAsync(del) { _ =>
            sender ! ExtractorOk()
            Observer("telemetry_extractor_spec_delete_success")
            takeSnapshot()
          }
        case _ =>
          Observer("telemetry_extractor_spec_delete_failure")
          sender ! None
      }

    case specId: String =>
      state.specs.get(specId) match {
        case Some(specs) =>
          logger.debug(s"found $specId")
          sender ! Some(specs)
          Observer("telemetry_extractor_spec_lookup_success")
        case _ =>
          Observer("telemetry_extractor_spec_lookup_failure")
          sender ! None
      }

    case _: SaveSnapshotSuccess =>
    case None =>
      logger.warn("unexpected None")
    case m =>
      logger.warn(s"unexpected message: $m")
      sender ! None

  }

  override def receiveRecover: Receive = {

    case spec: TelemetryExtractorSpec =>
      state = TelemetryExtractorSpecMap(state.specs + (spec.specId -> spec))
      Observer("reapplied_telemetry_extractor_spec_actor_command_from_jrnl")

    case del: DeleteSpec =>
      state = TelemetryExtractorSpecMap(state.specs - del.specId)
      Observer(
        "reapplied_telemetry_extractor_spec_actor_delete_command_from_jrnl")

    case SnapshotOffer(_, s: TelemetryExtractorSpecMap @unchecked) =>
      Observer("recovered_telemetry_extractor_spec_actor_state_from_snapshot")
      state = s

    case _: RecoveryCompleted =>
      Observer("resurrected_telemetry_extractor_spec_actor")
      logger.debug(s"${self.path}: Recovery completed. State: $state")

    case x =>
      logger.warn(s"unexpected recover msg: $x")

  }

}
