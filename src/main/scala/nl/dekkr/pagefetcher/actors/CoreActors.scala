package nl.dekkr.pagefetcher.actors

import akka.actor.Props
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

/**
 * This trait contains the nl.dekkr.pagefetcher.actors that make up our application; it can be mixed in with
 * ``BootedCore`` for running code or ``TestKit`` for unit and integration tests.
 */
trait CoreActors {
  this: Core =>
  val persistence = system.actorOf(Props[PersistenceActor], "persistence")

  implicit val timeout = Timeout(ConfigFactory.load().getInt("nl.dekkr.pagefetcher.api.timeout").seconds)

}
