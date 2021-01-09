package dtlaboratory.dtlab.ingest.actors

import akka.persistence.PersistentActor
import dtlaboratory.dtlab.ingest.observe.Observer
import dtlaboratory.dtlab.ingest.Conf._

abstract class PersistentActorBase[T] extends PersistentActor {

  var state: T

  override def persistenceId: String =
    persistIdRoot + "_" + self.path.toString.replace('-', '_')

  def takeSnapshot(): Unit = {
    if (lastSequenceNr % snapshotInterval == 0 && lastSequenceNr != 0) {
      saveSnapshot(state)
      Observer("actor_saved_state_snapshot")
    }
  }

}
