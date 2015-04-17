package nl.dekkr.pagefetcher

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

object Boot extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  val service = system.actorOf(Props[FrontendServiceActor], "page-fetch-service")

  implicit val timeout = Timeout(5.seconds)  //TODO move timeout to config [Issue #5]
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)    //TODO move interface & port to config [Issue #5]
}