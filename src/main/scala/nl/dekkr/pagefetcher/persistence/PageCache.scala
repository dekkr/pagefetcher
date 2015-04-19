package nl.dekkr.pagefetcher.persistence

import java.time.OffsetDateTime

import com.typesafe.scalalogging.Logger
import net.fwbrasil.activate.entity.Entity
import nl.dekkr.pagefetcher.model.PageUrl
import nl.dekkr.pagefetcher.persistence.persistenceContext._
import org.slf4j.LoggerFactory

import scala.languageFeature.{implicitConversions, postfixOps}

trait StoredPage extends Entity {
  val uri: String

  def invariantNameMustNotBeEmpty = invariant {
    uri != null && uri.nonEmpty
  }
}

/**
 * Entity for caching a page
 */
class PageCache(val uri: String, var content: Option[String] = None, var raw: Boolean = false, var createdAt: Long = OffsetDateTime.now().toEpochSecond) extends StoredPage {

  //implicit def offsetDateTimeToLong(offsetDateTime: OffsetDateTime): Long = offsetDateTime.toEpochSecond

  //implicit def longToOffsetDateTime(l: Long): OffsetDateTime = OffsetDateTime.ofInstant(Instant.ofEpochSecond(l), ZoneId.systemDefault())

}

object Storage {

  import scala.language.postfixOps

  private val logger = Logger(LoggerFactory.getLogger("[Page storage]"))


  def read(request: PageUrl): Option[PageCache] = {
    val notBefore: Long = request.maxAge match {
      case Some(minutes) =>
        logger.info(s"read  [url: ${request.url}] [age: $minutes minutes]")
        OffsetDateTime.now().minusMinutes(minutes).toEpochSecond
      case None =>
        logger.info(s"read  [url: ${request.url}] [age: unspecified]")
        0
    }
    transactional {
      val res2 = query {
        (entity: PageCache) =>
          where(
            (entity.uri :== request.url) :&&
              (entity.raw :== request.raw) :&&
              (entity.createdAt :>= notBefore)
          ) select entity orderBy (entity.createdAt desc) limit 1
      }
      if (res2.isEmpty) {
        logger.info(s"miss  [url: ${request.url}] [raw: ${request.raw.get}]")
        None
      } else {
        logger.info(s"hit   [url: ${res2.head.uri}] [raw: ${res2.head.raw}] [age: ${(OffsetDateTime.now().toEpochSecond - res2.head.createdAt) / 60} minutes]")
        Some(res2.head)
      }
    }
  }

  def write(url: String, content: Option[String], raw: Boolean): Unit = {
    logger.info(s"write [url: $url] [raw: $raw]")
    transactional {
      new PageCache(uri = url, content = content, raw = raw)
    }
  }

}