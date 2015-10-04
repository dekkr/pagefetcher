package nl.dekkr.pagefetcher.persistence

import java.time.OffsetDateTime

import scala.languageFeature.{implicitConversions, postfixOps}

/**
 * Entity for caching a page
 */
case class PageCache(uri: String, content: Option[String] = None, raw: Boolean = false, createdAt: Long = OffsetDateTime.now().toEpochSecond)
