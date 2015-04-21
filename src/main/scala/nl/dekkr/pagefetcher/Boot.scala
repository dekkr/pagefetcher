package nl.dekkr.pagefetcher

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import nl.dekkr.pagefetcher.actors.{BootedCore, CoreActors, RemoveOldPages}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

object Boot extends App with BootedCore with CoreActors {

  private val logger = Logger(LoggerFactory.getLogger("[PageFetcher]"))

  ConfigFactory.load().getInt("nl.dekkr.pagefetcher.persistence.maxStorageAge") match {
    case hours if hours > 0 =>
      logger.info(s"Automatic cleanup of content older the $hours hours")
      system.scheduler.schedule(10 seconds, 60 minutes, persistence, RemoveOldPages(hours))
    case _ =>
      logger.info("Automatic cleanup disabled")
  }

}