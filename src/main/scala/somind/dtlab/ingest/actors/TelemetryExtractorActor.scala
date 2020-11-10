package somind.dtlab.ingest.actors

import navicore.data.navipath.dsl.NaviPathSyntax._
import akka.persistence._
import com.fasterxml.jackson.databind.JsonNode
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.ingest.actors.functions.ExtractTelemetry
import somind.dtlab.ingest.models.{DeleteSpec, ExtractorOk, Specs, TelemetryExtractorSpecMap}
import somind.dtlab.ingest.observe.Observer

object TelemetryExtractorActor extends LazyLogging {
  def name: String = this.getClass.getName
}

class TelemetryExtractorActor
    extends PersistentActorBase[TelemetryExtractorSpecMap]
    with LazyLogging {

  override var state: TelemetryExtractorSpecMap = TelemetryExtractorSpecMap(
    specs = Map())

  override def receiveCommand: Receive = {

    case (specId: String, json: String) =>
      Observer("telemetry_extractor_object_request")
      val parsedJson = json.asJson
      self forward (specId, Seq(parsedJson))

    // extract telemetry from array of raw json
    case (specId: String, nodes: Seq[JsonNode] @unchecked) =>
      Observer("telemetry_extractor_objects_request")
      state.specs.get(specId) match {
        case Some(extractorSpecs) =>
          val telemetry = nodes.flatMap(ExtractTelemetry(_, extractorSpecs))

          if (telemetry.isEmpty)
            sender ! None
          else
            sender ! Some(telemetry)

        case _ =>
          logger.warn(s"did not find $specId for JsonNode extract.")
          sender ! None
      }

    // extract telemetry from raw json
    case (specId: String, node: JsonNode) =>
      Observer("telemetry_extractor_object_request")
      state.specs.get(specId) match {
        case Some(extractorSpecs) =>
          ExtractTelemetry(node, extractorSpecs) match {
            case telemetry if telemetry.nonEmpty =>
              sender ! Some(telemetry)
            case _ =>
              sender ! None
          }

        case _ =>
          logger.warn(s"did not find $specId for JsonNode extract.")
          sender ! None
      }

    // manage specs
    case specs: Specs @unchecked =>
      state.specs.get(specs.specs.head.specId) match {
        case Some(prev) =>
          logger.debug(s"create found existing ${specs.specs.head.specId}")
          sender ! Some(Specs(prev))
          Observer("telemetry_extractor_spec_create_conflict")
        case _ =>
          logger.debug(
            s"did not find specs for ${specs.specs.head.specId}.  creating...")
          state = TelemetryExtractorSpecMap(
            state.specs + (specs.specs.head.specId -> specs.specs))
          Observer("telemetry_extractor_spec_created")
          persistAsync(specs) { _ =>
            sender ! Some(specs)
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
          sender ! Some(Specs(specs))
          Observer("telemetry_extractor_spec_lookup_success")
        case _ =>
          Observer("telemetry_extractor_spec_lookup_failure")
          sender ! None
      }

    case _: SaveSnapshotSuccess =>
    case None =>
      logger.warn("unexpected receive None")
    case m =>
      logger.warn(s"unexpected message: $m")
      sender ! None

  }

  override def receiveRecover: Receive = {

    case specs: Specs @unchecked =>
      state = TelemetryExtractorSpecMap(
        state.specs + (specs.specs.head.specId -> specs.specs))
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
