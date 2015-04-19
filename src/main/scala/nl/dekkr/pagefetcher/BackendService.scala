package nl.dekkr.pagefetcher

import nl.dekkr.pagefetcher.model.PageUrl
import nl.dekkr.pagefetcher.persistence.Storage
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import spray.http.StatusCodes
import spray.routing.StandardRoute
import spray.routing.directives.RouteDirectives

import scala.util.{Failure, Success, Try}
import scalaj.http.Http

object BackendService {

  def getPage(request: PageUrl): StandardRoute = {
    Storage.getFromCache(request) match {
      case Some(page) =>
        page.content match {
          case Some(content) =>
            RouteDirectives.complete((StatusCodes.OK, content))
          case None =>
            RouteDirectives.complete(StatusCodes.NoContent)
        }
      case None =>
        fetchPage(request)
    }
  }

  private def fetchPage(request: PageUrl): StandardRoute = {
    Try(BackendService.pageContent(request.url).charset("UTF-8")) match {
      case Success(content) =>
        try {
          Storage.storeInCache(request.url, Some(content.asString), raw = true)
          val result =
            request.raw match {
              case Some(raw) if raw => content.asString
              case _ =>
                val cleaned = cleanContent(content.asString, request.url)
                Storage.storeInCache(request.url, Some(cleaned), raw = false)
                cleaned
            }
          RouteDirectives.complete((StatusCodes.OK, result))
        }
        catch {
          case e1: java.net.UnknownHostException =>
            RouteDirectives.complete((StatusCodes.NotFound, s"Host not found: ${request.url}"))
          case e3: Exception =>
            RouteDirectives.complete((StatusCodes.BadRequest, s"${e3.getMessage} - ${e3.getCause}"))
        }
      case Failure(e) =>
        RouteDirectives.complete((StatusCodes.BadRequest, e.getMessage))
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

  private val USER_AGENT: String = "Mozilla/5.0)"

  private def pageContent(uri: String) = Http(uri).header("User-Agent", USER_AGENT)

}
