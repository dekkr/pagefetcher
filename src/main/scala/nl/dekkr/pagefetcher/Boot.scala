package nl.dekkr.pagefetcher

import akka.http.Http
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import nl.dekkr.pagefetcher.actors.{BootedCore, CoreActors, RemoveOldPages}
import nl.dekkr.pagefetcher.services.{BackendService, FrontendService}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

object Boot extends App with BootedCore with CoreActors with FrontendService {


  private val logger = Logger(LoggerFactory.getLogger("[PageFetcher]"))
  val config = ConfigFactory.load()
  val CONFIG_BASE = "nl.dekkr.pagefetcher"

  implicit val backend = new BackendService()(persistence)


  startApi()
  startHousekeeping()


  def startHousekeeping(): Unit = {
    config.getInt(s"$CONFIG_BASE.persistence.maxStorageAge") match {
      case hours if hours > 0 =>
        logger.info(s"Automatic cleanup of content older the $hours hours")
        system.scheduler.schedule(10 seconds, 60 minutes, persistence, RemoveOldPages(hours))
      case _ =>
        logger.info("Automatic cleanup disabled")
    }
  }

  def startApi(): Unit = {
    implicit val materializer = ActorFlowMaterializer()
    val serverSource: Source[Http.IncomingConnection, Future[Http.ServerBinding]] =
      Http(system).bind(
        interface = config.getString(s"$CONFIG_BASE.api.interface"),
        port = config.getInt(s"$CONFIG_BASE.api.port"))

    //val bindingFuture: Future[Http.ServerBinding] =
    serverSource.to(Sink.foreach { connection =>
      logger.debug("Accepted new connection from " + connection.remoteAddress)
      connection handleWithSyncHandler requestHandler
    }).run()
  }

}