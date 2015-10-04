package nl.dekkr.pagefetcher

import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import nl.dekkr.pagefetcher.actors.{BootedCore, CoreActors}
import nl.dekkr.pagefetcher.messages.RemoveOldPages
import nl.dekkr.pagefetcher.model.Constants
import nl.dekkr.pagefetcher.services.{BackendService, FrontendService}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

object Boot extends App with BootedCore with CoreActors with FrontendService with Constants {


  private val logger = Logger(LoggerFactory.getLogger("PageFetcher"))
  val config = ConfigFactory.load()

  implicit val backend = new BackendService()(persistence)

  logger.info(s"Setting up database connection...")
  backend.initBackEnd match {
    case Success(_) => logger.info("Database ready")
    case Failure(_) =>
  }

  startApi()
  startHousekeeping()
  logger.info(s"Done booting...")

  def startHousekeeping(): Unit = {
    logger.info(s"Scheduling housekeeping...")
    config.getInt(s"$CONFIG_BASE.persistence.maxStorageAge") match {
      case hours if hours > 0 =>
        logger.info(s"Automatic cleanup of content older the $hours hours")
        system.scheduler.schedule(10 seconds, 60 minutes, persistence, RemoveOldPages(hours))
      case _ =>
        logger.info("Automatic cleanup disabled")
    }
  }

  def startApi(): Unit = {
    logger.info(s"Enabling REST interface...")
    implicit val materializer = ActorMaterializer()
    //implicit val fm =
    val serverSource: Source[Http.IncomingConnection, Future[Http.ServerBinding]] =
      Http(system).bind(
        interface = config.getString(s"$CONFIG_BASE.api.interface"),
        port = config.getInt(s"$CONFIG_BASE.api.port"))

    serverSource.to(Sink.foreach { connection =>
      logger.debug("Accepted new connection from " + connection.remoteAddress)
      connection handleWithSyncHandler requestHandler
    }).run()
  }

}