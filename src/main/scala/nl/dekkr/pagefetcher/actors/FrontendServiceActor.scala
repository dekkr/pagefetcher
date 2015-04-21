package nl.dekkr.pagefetcher.actors

import akka.actor.Actor
import nl.dekkr.pagefetcher.services.FrontendService

class FrontendServiceActor extends Actor with FrontendService {
  def actorRefFactory = context

  def receive = runRoute(myRoute)
}
