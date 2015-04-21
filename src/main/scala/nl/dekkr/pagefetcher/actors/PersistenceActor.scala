package nl.dekkr.pagefetcher.actors

import akka.actor.Actor
import akka.event.Logging
import nl.dekkr.pagefetcher.services.StorageService

case class RemoveOldPages(nrOfHours: Int)

class PersistenceActor extends Actor {

  val log = Logging(context.system, this)

  def receive = {
    case RemoveOldPages(nrOfHours) =>
      log.info(s"Cleaning everthing older than $nrOfHours hours.")
      StorageService.deleteOlderThan(nrOfHours)
  }

}