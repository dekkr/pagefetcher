package nl.dekkr.pagefetcher.services

import nl.dekkr.pagefetcher.model.PageUrl
import spray.http.MediaTypes._
import spray.routing._

trait FrontendService extends HttpService {

  // TODO add swagger annotations [Issue #7]
  val myRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) {
          complete {
            <html>
              <head>
                <title>Page Fetcher by DekkR projects</title>
              </head>
              <body>
                <h1>Page Fetcher</h1>
                <p>Ready to serve a page.</p>
              </body>
            </html>
          }
        }
      }
    } ~
      path("v1" / "page") {
        get {
          parameters(('url.as[String], 'maxAge.as[Option[Int]], 'raw.as[Option[Boolean]])).as(PageUrl) { pageRequest =>
            respondWithMediaType(`text/html`) {
              BackendService.getPage(pageRequest)
            }
          }
        }
      }
}