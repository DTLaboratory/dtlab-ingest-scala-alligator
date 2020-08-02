package somind.dtlab.ingest.actors

import akka.persistence._
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.ingest.models._
import somind.dtlab.ingest.observe.Observer

object TelemetryExtractorActor extends LazyLogging {
  def name: String = this.getClass.getName
}

class TelemetryExtractorActor extends PersistentActorBase[TelemetryExtractorSpecMap] with LazyLogging {

  // ejs todo: spec is a collection of extractor specs  - map of a seq
  // ejs todo: spec is a collection of extractor specs  - map of a seq
  // ejs todo: spec is a collection of extractor specs  - map of a seq
  // ejs todo: spec is a collection of extractor specs  - map of a seq
  // ejs todo: spec is a collection of extractor specs  - map of a seq
  // ejs todo: spec is a collection of extractor specs  - map of a seq
  // ejs todo: spec is a collection of extractor specs  - map of a seq
  // ejs todo: spec is a collection of extractor specs  - map of a seq
  // ejs todo: spec is a collection of extractor specs  - map of a seq
  // ejs todo: spec is a collection of extractor specs  - map of a seq
  // ejs todo: spec is a collection of extractor specs  - map of a seq
  override var state: TelemetryExtractorSpecMap = TelemetryExtractorSpecMap(specs = Map())

  override def receiveCommand: Receive = {

    case spec: TelemetryExtractorSpec =>
      state.specs.get(spec.name) match {
        case Some(prev) =>
          logger.debug(s"create found existing ${spec.name}")
          sender ! Some(prev)
          Observer("telemetry_extractor_spec_create_conflict")
        case _ =>
          logger.debug(s"did not find ${spec.name}.  creating...")
          state = TelemetryExtractorSpecMap(state.specs + (spec.name -> spec))
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
        case Some(spec) =>
          logger.debug(s"found ${spec.name}")
          sender ! Some(spec)
          Observer("telemetry_extractor_spec_lookup_success")
        case _ =>
          Observer("telemetry_extractor_spec_lookup_failure")
          sender ! None
      }

    case _: SaveSnapshotSuccess =>
    case m =>
      logger.warn(s"unexpected message: $m")
      sender ! None

  }

  override def receiveRecover: Receive = {

    case spec: TelemetryExtractorSpec =>
      state = TelemetryExtractorSpecMap(state.specs + (spec.name -> spec))
      Observer("reapplied_telemetry_extractor_spec_actor_command_from_jrnl")

    case del: DeleteSpec =>
      state = TelemetryExtractorSpecMap(state.specs - del.specId)
      Observer("reapplied_telemetry_extractor_spec_actor_delete_command_from_jrnl")

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
