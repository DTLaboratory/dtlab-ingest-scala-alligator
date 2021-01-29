package dtlaboratory.dtlab.ingest.actors

import akka.persistence._
import com.fasterxml.jackson.databind.JsonNode
import com.typesafe.scalalogging.LazyLogging
import dtlaboratory.dtlab.ingest.observe.Observer
import navicore.data.navipath.dsl.NaviPathSyntax._
import dtlaboratory.dtlab.ingest.Conf._
import dtlaboratory.dtlab.ingest.models._

/**
 * Extractor API lets you define how to turn raw json input into DtLab-ready telemetry messages.
 * The "object" extractor expects to have to pull an array of collections of observations from
 * the raw input.  An example would be a fleet controller sending batches of odometer and speed
 * telemetry on behalf of multiple vehicles in a single json message.
 *
 * This actor keeps all the specs that instruct it how to pull the data out of the raw input
 * in its state by specId key.
 *
 *  TODO: combine the object extractor and telemetry exatractor routes and implementations.
 *  The route need not be different - the spec can imply to repost array contents.
 *
 *  TODO: hide the JSON specifics in helper functions
 *  we will support csv and xml and who-knows-what-else
 */
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
              telemetryExtractor forward (spec.telSpecId, objects, Some(parsedJson))
            case _ =>
              logger.warn(s"no data extracted for spec $specId from data: $json")
              sender() ! ExtractorNoData()
          }
        case _ =>
          sender() ! ExtractorErr(s"object extractor spec '$specId' not found")
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
