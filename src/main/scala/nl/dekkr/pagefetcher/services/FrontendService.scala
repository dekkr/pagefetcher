package nl.dekkr.pagefetcher.services

import akka.http.model.HttpMethods._
import akka.http.model.StatusCodes._
import akka.http.model._
import nl.dekkr.pagefetcher.model.{NoContent, _}

import scala.language.implicitConversions

trait FrontendService {
  implicit val backend: BackendSystem

  val requestHandler: HttpRequest => HttpResponse = {
    case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
      HttpResponse(
        entity = HttpEntity(MediaTypes.`text/html`,
          <html>
            <head>
              <title>Page Fetcher by DekkR projects</title>
            </head>
            <body>
              <h1>Page Fetcher</h1>
              <p>Ready to serve a page.</p>
            </body>
          </html>.buildString(stripComments = true)
        ))

    case HttpRequest(GET, uri, _, _, _) if uri.path.startsWith(Uri.Path("/v1/page")) => {
      uri.query.get("url") match {
        case Some(url) =>
          try {
            backend.getContent(uri) match {
              case ExistingContent(content) => HttpResponse(entity = HttpEntity(MediaTypes.`text/html`, content))
              case NewContent(content) => HttpResponse(entity = HttpEntity(MediaTypes.`text/html`, content))
              case NoContent() => HttpResponse(NotFound, entity = "Not found")
              case UnknownHost(host) => HttpResponse(NotFound, entity = s"Host $host Not found")
              case Error(msg) => HttpResponse(BadRequest, entity = s"$msg")
              case _ => HttpResponse(BadRequest, entity = s"Bad request")
            }
          }
          catch {
            case e: Exception => HttpResponse(BadRequest, entity = s"${e.getMessage}")
          }
        case None =>
          HttpResponse(NotFound, entity = "Missing url!")
      }
    }
    case _: HttpRequest =>
      HttpResponse(NotFound, entity = "Unknown resource!")
  }

  implicit def uri2PageUri(uri: Uri): PageUrl = {
    val maxAge: Option[Int] = uri.query.get("maxAge") match {
      case Some(value: String) => Some(value.toInt)
      case None => Some(1440)
    }
    val raw: Option[Boolean] = uri.query.get("raw") match {
      case Some(value: String) => Some(value.toBoolean)
      case None => Some(false)
    }
    PageUrl(url = uri.query.get("url").getOrElse(""), maxAge = maxAge, raw = raw)
  }

}