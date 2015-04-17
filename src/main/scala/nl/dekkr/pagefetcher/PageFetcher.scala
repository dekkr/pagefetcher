package nl.dekkr.pagefetcher

import nl.dekkr.pagefetcher.model.PageUrl
import spray.http.StatusCodes
import spray.routing.StandardRoute
import spray.routing.directives.RouteDirectives

import scala.util.{Failure, Success, Try}
import scalaj.http.Http


object PageFetcher {

  def getPage(request: PageUrl) : StandardRoute = {
    // TODO Check for the page in the cache first, using the maxAge parameter [Issue #2]
    Try(PageFetcher.pageContent(request.url).charset("UTF-8")) match {
      case Success(content) =>
        try {
          val result =
          request.raw match {
            case Some(raw) if !raw =>
              // TODO clean up the content, with some html tidy library [Issue #4]
              content.asString
            case _ =>  content.asString
          }
          RouteDirectives.complete((StatusCodes.OK, result))
        }
        catch {
          case e1: java.net.UnknownHostException =>
            RouteDirectives.complete((StatusCodes.NotFound, s"Host not found: ${request.url}"))
          case e2: java.io.FileNotFoundException =>
            RouteDirectives.complete((StatusCodes.NotFound, s"Page not found: ${request.url}"))
          case e3: Exception =>
            RouteDirectives.complete((StatusCodes.BadRequest, s"${e3.getMessage} - ${e3.getCause}"))
        }
      case Failure(e) =>
        RouteDirectives.complete((StatusCodes.BadRequest, e.getMessage))
    }
  }


  private val USER_AGENT: String = "Mozilla/5.0)"

  private def pageContent(uri: String) = Http(uri).header("User-Agent", USER_AGENT)

}
