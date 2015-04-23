package nl.dekkr.pagefetcher.actors

import akka.actor.Actor
import akka.event.Logging
import nl.dekkr.pagefetcher.model.PageUrl
import nl.dekkr.pagefetcher.services.BackendService

class FetchActor extends Actor {

  val log = Logging(context.system, this)

  def receive = {
    case PageUrl(url, maxAge, raw) =>
      BackendService.getPage(PageUrl(url, maxAge, raw))

  }

}