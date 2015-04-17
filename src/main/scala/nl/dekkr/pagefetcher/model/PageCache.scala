package nl.dekkr.pagefetcher.model

import java.time.OffsetDateTime
import org.apache.commons.validator.routines.UrlValidator

/**
 * Entity for caching a page
 */
case class PageCache(id: Option[Int] = None, uri: String, content: Option[String] = None, createdAt: OffsetDateTime)

/*
 * Entity for the page request
 */
case class PageUrl(url : String, maxAge : Option[Int] = Some(1440), raw : Option[Boolean] = None) {
  require(!url.isEmpty, "url must not be empty")
  require(new UrlValidator(Array("http","https")).isValid(url), "url is invalid")
}
