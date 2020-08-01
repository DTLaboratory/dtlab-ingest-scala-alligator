package somind.dtlab.ingest.actors

import akka.persistence._
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.ingest.models._
import somind.dtlab.ingest.observe.Observer

object ExtractorActor extends LazyLogging {
  def name: String = this.getClass.getName
}

class ExtractorActor extends PersistentActorBase[ExtractorSpecMap] with LazyLogging {

  override var state: ExtractorSpecMap = ExtractorSpecMap(specs = Map())

  override def receiveCommand: Receive = {

    case spec: ExtractorSpec =>
      state.specs.get(spec.name) match {
        case Some(prev) =>
          sender ! Some(prev)
        case _ =>
          state = ExtractorSpecMap(state.specs + (spec.name -> spec))
          persistAsync(spec) { _ =>
            sender ! Some(spec)
            takeSnapshot()
          }
      }

    case DeleteSpec(specId) =>
      state.specs.get(specId) match {
        case Some(_) =>
          state = ExtractorSpecMap(state.specs - specId)
          sender ! ExtractorOk()
        case _ =>
          sender ! None
      }

    case specId: String =>
      state.specs.get(specId) match {
        case Some(spec) =>
          sender ! Some(spec)
        case _ =>
          sender ! None
      }

    case _: SaveSnapshotSuccess =>
    case m =>
      logger.warn(s"unexpected message: $m")
      sender ! None

  }

  override def receiveRecover: Receive = {

    case spec: ExtractorSpec =>
      state = ExtractorSpecMap(state.specs + (spec.name -> spec))
      Observer("reapplied_extractor_spec_actor_command_from_jrnl")

    case SnapshotOffer(_, s: ExtractorSpecMap @unchecked) =>
      Observer("recovered_extractor_spec_actor_state_from_snapshot")
      state = s

    case _: RecoveryCompleted =>
      Observer("resurrected_extractor_spec_actor")
      logger.debug(s"${self.path}: Recovery completed. State: $state")

    case x =>
      logger.warn(s"unexpected recover msg: $x")

  }

}
