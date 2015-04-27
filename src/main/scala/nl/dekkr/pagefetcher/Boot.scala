package nl.dekkr.pagefetcher

import akka.http.Http
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import nl.dekkr.pagefetcher.actors.{BootedCore, CoreActors, RemoveOldPages}
import nl.dekkr.pagefetcher.services.FrontendService
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

object Boot extends App with BootedCore with CoreActors with FrontendService {

  implicit val materializer = ActorFlowMaterializer()

  private val logger = Logger(LoggerFactory.getLogger("[PageFetcher]"))
  val config = ConfigFactory.load()

  config.getInt("nl.dekkr.pagefetcher.persistence.maxStorageAge") match {
    case hours if hours > 0 =>
      logger.info(s"Automatic cleanup of content older the $hours hours")
      system.scheduler.schedule(10 seconds, 60 minutes, persistence, RemoveOldPages(hours))
    case _ =>
      logger.info("Automatic cleanup disabled")
  }

  val serverSource: Source[Http.IncomingConnection, Future[Http.ServerBinding]] =
    Http(system).bind(
      interface = config.getString("nl.dekkr.pagefetcher.api.interface"),
      port = config.getInt("nl.dekkr.pagefetcher.api.port"))

  val bindingFuture: Future[Http.ServerBinding] =
    serverSource.to(Sink.foreach { connection =>
      logger.info("Accepted new connection from " + connection.remoteAddress)
      connection handleWithSyncHandler requestHandler
    }).run()

}