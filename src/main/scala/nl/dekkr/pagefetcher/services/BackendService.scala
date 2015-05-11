package nl.dekkr.pagefetcher.services

import akka.actor.ActorRef
import nl.dekkr.pagefetcher.actors.StorePage
import nl.dekkr.pagefetcher.model._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.util.{Failure, Success, Try}
import scalaj.http._

class BackendService(implicit val persistence: ActorRef) extends BackendSystem {


  private val USER_AGENT: String = "Mozilla/5.0)"
  private val CHARSET: String = "UTF-8"

  def getContent(request: PageUrl): BackendResult = {
    StorageService.read(request) match {
      case Some(page) =>
        page.content match {
          case Some(content) => ExistingContent(content)
          case None => NoContent()
        }
      case None =>
        Try(this.pageContent(request.url).charset(CHARSET)) match {
          case Success(content) =>
            processRequest(request, content)
          case Failure(e) => Error(s"${e.getMessage}")
        }
    }
  }

  private def processRequest(request: PageUrl, content: HttpRequest): BackendResult = {
    val rawRequested = request.raw.getOrElse(false)
    try {
      val rawContent = content.asString.body
      val result = if (rawRequested) {
        rawContent
      } else {
        cleanContent(rawContent, request.url)
      }
      persistence ! StorePage(url = request.url, Some(result), raw = rawRequested)
      NewContent(result)
    }
    catch {
      case e1: java.net.UnknownHostException => UnknownHost(request.url)
      case e3: Exception => Error(s"${e3.getMessage} - ${e3.getCause}")
    }
  }

  private def cleanContent(content: String, url: String): String = {
    val htmlParsed = Jsoup.parse(content).normalise()
    htmlParsed.setBaseUri(url)
    absolutePaths(absolutePaths(htmlParsed, "src"), "href").html()
  }

  private def absolutePaths(htmlParsed: Document, attribute: String): Document = {
    val itr = htmlParsed.getElementsByAttribute(attribute).iterator()
    while (itr.hasNext) {
      val element = itr.next()
      element.attr(attribute, element.attr(s"abs:$attribute"))
    }
    htmlParsed
  }

  private def pageContent(uri: String) = Http(uri).option(HttpOptions.followRedirects(shouldFollow = true)).header("User-Agent", USER_AGENT)

}

