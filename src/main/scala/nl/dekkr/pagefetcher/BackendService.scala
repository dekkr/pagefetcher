package nl.dekkr.pagefetcher

import nl.dekkr.pagefetcher.model.PageUrl
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import spray.http.StatusCodes
import spray.routing.StandardRoute
import spray.routing.directives.RouteDirectives

import scala.util.{Failure, Success, Try}
import scalaj.http.Http


object BackendService {

  def getPage(request: PageUrl): StandardRoute = {
    // TODO Check for the page in the cache first, using the maxAge parameter [Issue #2]
    Try(BackendService.pageContent(request.url).charset("UTF-8")) match {
      case Success(content) =>
        try {
          val result =
            request.raw match {
              case Some(raw) if raw => content.asString
              case _ =>
                val htmlParsed = Jsoup.parse(content.asString).normalise()
                htmlParsed.setBaseUri(request.url)
                absolutePaths(absolutePaths(htmlParsed, "src"), "href").html()
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
