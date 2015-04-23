package nl.dekkr.pagefetcher.actors

import akka.actor.Actor
import akka.event.Logging
import nl.dekkr.pagefetcher.services.StorageService

case class RemoveOldPages(nrOfHours: Int)

case class StorePage(url: String, content: Option[String], raw: Boolean)

class PersistenceActor extends Actor {

  val log = Logging(context.system, this)

  def receive = {
    case RemoveOldPages(nrOfHours) =>
      log.info(s"Cleaning everything older than $nrOfHours hours.")
      StorageService.deleteOlderThan(nrOfHours)

    case StorePage(url, content, raw) =>
      StorageService.write(url, content, raw)
  }

}