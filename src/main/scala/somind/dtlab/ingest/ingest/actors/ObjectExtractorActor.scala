package somind.dtlab.ingest.ingest.actors

import akka.persistence._
import com.fasterxml.jackson.databind.JsonNode
import com.typesafe.scalalogging.LazyLogging
import navicore.data.navipath.dsl.NaviPathSyntax._
import somind.dtlab.ingest.ingest.Conf._
import somind.dtlab.ingest.ingest.models._
import somind.dtlab.ingest.ingest.observe.Observer

class ObjectExtractorActor
    extends PersistentActorBase[ObjectExtractorSpecMap]
    with LazyLogging {

  override var state: ObjectExtractorSpecMap = ObjectExtractorSpecMap(
    specs = Map())

  override def receiveCommand: Receive = {

    case (specId: String, json: String) =>
      state.specs.get(specId) match {
        case Some(spec) =>
          val parsedJson = json.asJson
          parsedJson.query[List[JsonNode]](spec.path) match {
            case Some(objects: List[JsonNode]) =>
              telemetryExtractor forward (spec.telSpecId, objects)
            case _ =>
              sender() ! ExtractorErr("extractor did not extract any objects")
          }
        case _ =>
          sender() ! ExtractorErr("object extractor spec not found")
      }

    case spec: ObjectExtractorSpec =>
      state.specs.get(spec.specId) match {
        case Some(prev) =>
          logger.debug(s"create found existing ${spec.specId}")
          sender ! Some(prev)
          Observer("object_extractor_spec_create_conflict")
        case _ =>
          logger.debug(s"did not find ${spec.specId}.  creating...")
          state = ObjectExtractorSpecMap(state.specs + (spec.specId -> spec))
          Observer("object_extractor_spec_created")
          persistAsync(spec) { _ =>
            sender ! Some(spec)
            takeSnapshot()
          }
      }

    case del: DeleteSpec =>
      state.specs.get(del.specId) match {
        case Some(_) =>
          state = ObjectExtractorSpecMap(state.specs - del.specId)
          persistAsync(del) { _ =>
            sender ! ExtractorOk()
            Observer("object_extractor_spec_delete_success")
            takeSnapshot()
          }
        case _ =>
          Observer("object_extractor_spec_delete_failure")
          sender ! None
      }

    case specId: String =>
      state.specs.get(specId) match {
        case Some(spec) =>
          logger.debug(s"found ${spec.specId}")
          sender ! Some(spec)
          Observer("object_extractor_spec_lookup_success")
        case _ =>
          Observer("object_extractor_spec_lookup_failure")
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

    case spec: ObjectExtractorSpec =>
      state = ObjectExtractorSpecMap(state.specs + (spec.specId -> spec))
      Observer("reapplied_object_extractor_spec_actor_command_from_jrnl")

    case del: DeleteSpec =>
      state = ObjectExtractorSpecMap(state.specs - del.specId)
      Observer("reapplied_object_extractor_spec_actor_delete_command_from_jrnl")

    case SnapshotOffer(_, s: ObjectExtractorSpecMap @unchecked) =>
      Observer("recovered_object_extractor_spec_actor_state_from_snapshot")
      state = s

    case _: RecoveryCompleted =>
      Observer("resurrected_object_extractor_spec_actor")
      logger.debug(s"${self.path}: Recovery completed. State: $state")

    case x =>
      logger.warn(s"unexpected recover msg: $x")

  }

}
