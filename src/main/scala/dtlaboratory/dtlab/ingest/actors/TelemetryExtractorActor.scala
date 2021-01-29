package dtlaboratory.dtlab.ingest.actors

import akka.persistence._
import com.fasterxml.jackson.databind.JsonNode
import com.typesafe.scalalogging.LazyLogging
import dtlaboratory.dtlab.ingest.actors.functions.jsonextractor
import dtlaboratory.dtlab.ingest.actors.functions.jsonextractor.ExtractJsonTelemetry
import dtlaboratory.dtlab.ingest.models._
import dtlaboratory.dtlab.ingest.observe.Observer
import dtlaboratory.dtlab.ingest.routes.functions.GetDtType
import navicore.data.navipath.dsl.NaviPathSyntax._

object TelemetryExtractorActor extends LazyLogging {
  def name: String = this.getClass.getName
}

/**
 * Extractor API lets you define how to turn raw json input into DtLab telemetry messages.
 *
 * This actor keeps all the specs that instruct it how to pull the data out of the raw input
 * in its state by specId key.
 *
 * This implementation expects no arrays, just unique observations.  IE: one device is reported.
 */
class TelemetryExtractorActor
    extends PersistentActorBase[TelemetryExtractorSpecMap]
    with LazyLogging {

  override var state: TelemetryExtractorSpecMap = TelemetryExtractorSpecMap(
    specs = Map())

  def getIndexedValueSpec(v: NamedValueSpec): IndexedValueSpec = {
    def getIdx(name: String, typeId: String): Int = {
      GetDtType(typeId) match {
        case Some(dttype) =>
          dttype.props match {
            case Some(props) =>
              val idx = props.indexOf(name)
              logger.debug(s"looked up dttype $typeId and idx for $name is $idx")
              idx
            case _ =>
              logger.warn(s"dttype $typeId has no props")
              -1
          }
        case _ =>
          logger.warn(s"dttype $typeId not found")
          -1
      }
    }
    val idx = getIdx(v.name, v.typeId)
    IndexedValueSpec(idx, v.path, v.valueType, v.extractZeros)
  }

  override def receiveCommand: Receive = {

    case (specId: String, json: String) =>
      Observer("telemetry_extractor_object_request")
      val parsedJson = json.asJson
      self forward (specId, Seq(parsedJson), None)

    // extract telemetry from array of raw json
    case (specId: String,
          nodes: Seq[JsonNode] @unchecked,
          outerNode: Option[JsonNode] @unchecked) =>
      Observer("telemetry_extractor_objects_request")
      state.specs.get(specId) match {
        case Some(extractorSpecs) =>
          val telemetry =
            nodes.flatMap(jsonextractor.ExtractJsonTelemetry(_, outerNode, extractorSpecs))

          if (telemetry.isEmpty)
            sender ! ExtractorNoData()
          else
            sender ! Some(telemetry)

        case _ =>
          logger.warn(s"did not find $specId spec for JsonNode extract.")
          sender ! None
      }

    // extract telemetry from raw json
    case (specId: String, node: JsonNode) =>
      logger.debug(s"applying raw json to spec $specId to extract telemetry")
      Observer("telemetry_extractor_object_request")
      state.specs.get(specId) match {
        case Some(extractorSpecs) =>
          jsonextractor.ExtractJsonTelemetry(node, None, extractorSpecs) match {
            case telemetry if telemetry.nonEmpty =>
              sender ! Some(telemetry)
            case _ =>
              sender ! None
          }

        case _ =>
          logger.warn(s"did not find $specId for JsonNode extract.")
          sender ! None
      }

    // manage specs - preprocess named value specs into idx value specs
    case specs: NamedSpecs =>
      val idxValueSpecs: Seq[Seq[IndexedValueSpec]] =
        specs.specs.map(s => s.values.map(getIndexedValueSpec))
      val bad: Option[IndexedValueSpec] =
        idxValueSpecs.flatten.find(_.idx == -1)

      bad match {
        case Some(valueSpec) =>
          sender() ! ExtractorErr(
            s"no type field found for path ${valueSpec.path} for specId ${specs.specs.head.specId}")
        case _ =>
          val zs: Seq[(NamedTelemetryExtractorSpec, Seq[IndexedValueSpec])] =
            specs.specs.zip(idxValueSpecs)

          self forward IndexedSpecs(specs = zs.map(pair => {
            val (nspec: NamedTelemetryExtractorSpec,
                 valueSpecs: Seq[IndexedValueSpec]) = pair
            IndexedTelemetryExtractorSpec(
              nspec.specId,
              nspec.paths,
              valueSpecs,
              nspec.datetimePath,
              nspec.datetimeFmt,
              nspec.created
            )
          }))
      }

    // manage specs
    case specs: IndexedSpecs =>
      state.specs.get(specs.specs.head.specId) match {
        case Some(prev) =>
          logger.debug(s"create found existing ${specs.specs.head.specId}")
          sender ! Some(IndexedSpecs(prev))
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
          sender ! Some(IndexedSpecs(specs))
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

    case specs: IndexedSpecs @unchecked =>
      state = TelemetryExtractorSpecMap(
        state.specs + (specs.specs.head.specId -> specs.specs))
      logger.debug(s"recovered spec map of size ${specs.specs.length}")
      Observer("reapplied_telemetry_extractor_spec_actor_command_from_jrnl")

    case del: DeleteSpec =>
      state = TelemetryExtractorSpecMap(state.specs - del.specId)
      Observer(
        "reapplied_telemetry_extractor_spec_actor_delete_command_from_jrnl")

    case SnapshotOffer(_, s: TelemetryExtractorSpecMap @unchecked) =>
      Observer("recovered_telemetry_extractor_spec_actor_state_from_snapshot")
      state = s
      logger.debug(
        s"recovered snapshot of spec map of size ${state.specs.size}")

    case _: RecoveryCompleted =>
      Observer("resurrected_telemetry_extractor_spec_actor")
      logger.debug(s"${self.path}: Recovery completed. State: $state")

    case x =>
      logger.warn(s"unexpected recover msg: $x")

  }

}
