package nl.dekkr.pagefetcher.model

import java.time.OffsetDateTime

/**
 * Entity for caching a page
 */
case class PageCache(id: Option[Int] = None, uri: String, content: Option[String] = None, createdAt: OffsetDateTime)

/*
 * Entity for the page request
 */
case class PageUrl(url : String, maxAge : Option[Int], raw : Option[Boolean]) {
  require(!url.isEmpty, "url must not be empty")
  require(url.startsWith("http://") || url.startsWith("https://"),  "url is invalid")
  //TODO add more comprehensive URL check [Issue #6]
}
