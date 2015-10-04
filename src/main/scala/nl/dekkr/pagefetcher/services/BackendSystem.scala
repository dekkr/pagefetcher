package nl.dekkr.pagefetcher.services

import akka.actor.ActorRef
import nl.dekkr.pagefetcher.model.{BackendResult, PageUrl}

trait BackendSystem {
  implicit val persistence: ActorRef

  def getContent(request: PageUrl, charSet: Option[String], userAgent: Option[String]): BackendResult

}
