package somind.dtlab.ingest.ingest.actors

import akka.persistence._
import com.fasterxml.jackson.databind.JsonNode
import com.typesafe.scalalogging.LazyLogging
import navicore.data.navipath.dsl.NaviPathSyntax._
import somind.dtlab.ingest.ingest.actors.functions.CalculatePath
import somind.dtlab.ingest.ingest.models.{DeleteSpec, ExtractorOk, Specs, TelemetryExtractorSpecMap}
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
        case Some(extractorSpecs) =>
          extractorSpecs.foreach(extractorSpec => {

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
          })
        case _ =>
          logger.warn(s"did not find $specId for JsonNode extract.")
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
      logger.warn("unexpected None")
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
