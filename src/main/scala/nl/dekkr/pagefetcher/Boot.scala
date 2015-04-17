package nl.dekkr.pagefetcher

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import com.typesafe.config.ConfigFactory
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

object Boot extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  val service = system.actorOf(Props[FrontendServiceActor], "page-fetch-service")

  implicit val timeout = Timeout(ConfigFactory.load().getInt("nl.dekkr.pagefetcher.api.timeout").seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(service,
    interface = ConfigFactory.load().getString("nl.dekkr.pagefetcher.api.host"),
    port = ConfigFactory.load().getInt("nl.dekkr.pagefetcher.api.port"))
}