package nl.dekkr.pagefetcher.actors

import akka.actor.Props

/**
 * This trait contains the nl.dekkr.pagefetcher.actors that make up our application; it can be mixed in with
 * ``BootedCore`` for running code or ``TestKit`` for unit and integration tests.
 */
trait CoreActors {
  this: Core =>
  implicit val persistence = system.actorOf(Props[PersistenceActor], "persistence")

}
