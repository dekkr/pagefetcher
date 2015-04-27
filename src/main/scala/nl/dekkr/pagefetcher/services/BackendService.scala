package nl.dekkr.pagefetcher.services

import nl.dekkr.pagefetcher.model._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.util.{Failure, Success, Try}
import scalaj.http.Http

object BackendService {

  private val USER_AGENT: String = "Mozilla/5.0)"

  def getPage(request: PageUrl): BackendResult = {
    StorageService.read(request) match {
      case Some(page) =>
        page.content match {
          case Some(content) => ExistingContent(content)
          case None => NoContent()
        }
      case None => fetchPage(request)
    }
  }

  private def fetchPage(request: PageUrl): BackendResult = {
    Try(BackendService.pageContent(request.url).charset("UTF-8")) match {
      case Success(content) =>
        try {
          val result =
            request.raw match {
              case Some(raw) if raw =>
                StorageService.write(request.url, Some(content.asString), raw = true)
                content.asString
              case _ =>
                val cleaned = cleanContent(content.asString, request.url)
                //persistence ! StorePage(request.url, Some(cleaned), raw = false)
                StorageService.write(request.url, Some(cleaned), raw = false)
                cleaned
            }
          NewContent(result)
        }
        catch {
          case e1: java.net.UnknownHostException => UnknownHost(request.url)
          case e3: Exception => Error(s"${e3.getMessage} - ${e3.getCause}")
        }
      case Failure(e) => Error(s"${e.getMessage}")
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

  private def pageContent(uri: String) = Http(uri).header("User-Agent", USER_AGENT)

}
