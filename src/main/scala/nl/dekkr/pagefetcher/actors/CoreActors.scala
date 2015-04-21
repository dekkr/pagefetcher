package nl.dekkr.pagefetcher.actors

import akka.actor.Props
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import spray.can.Http

import scala.concurrent.duration._

/**
 * This trait contains the nl.dekkr.hoppr.actors that make up our application; it can be mixed in with
 * ``BootedCore`` for running code or ``TestKit`` for unit and integration tests.
 */
trait CoreActors {
  this: Core =>
  val persistence = system.actorOf(Props[PersistenceActor], "persistence")

  val service = system.actorOf(Props[FrontendServiceActor], "rest-service")

  implicit val timeout = Timeout(ConfigFactory.load().getInt("nl.dekkr.pagefetcher.api.timeout").seconds)

  IO(Http) ? Http.Bind(
    service,
    interface = ConfigFactory.load().getString("nl.dekkr.pagefetcher.api.host"),
    port = ConfigFactory.load().getInt("nl.dekkr.pagefetcher.api.port")
  )

}
