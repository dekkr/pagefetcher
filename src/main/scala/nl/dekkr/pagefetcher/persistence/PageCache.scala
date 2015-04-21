package nl.dekkr.pagefetcher.persistence

import java.time.OffsetDateTime

import scala.languageFeature.{implicitConversions, postfixOps}

/**
 * Entity for caching a page
 */
class PageCache(val uri: String, var content: Option[String] = None, var raw: Boolean = false, var createdAt: Long = OffsetDateTime.now().toEpochSecond) extends StoredPage {

  //implicit def offsetDateTimeToLong(offsetDateTime: OffsetDateTime): Long = offsetDateTime.toEpochSecond

  //implicit def longToOffsetDateTime(l: Long): OffsetDateTime = OffsetDateTime.ofInstant(Instant.ofEpochSecond(l), ZoneId.systemDefault())

}

