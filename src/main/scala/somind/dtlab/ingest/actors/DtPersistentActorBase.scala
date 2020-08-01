package somind.dtlab.ingest.actors

import akka.persistence.PersistentActor
import somind.dtlab.ingest.Conf._
import somind.dtlab.ingest.observe.Observer

// Dt Persistent Actor Base
abstract class DtPersistentActorBase[T]
    extends DtActorBase
    with PersistentActor {

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
