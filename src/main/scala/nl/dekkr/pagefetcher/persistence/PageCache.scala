package nl.dekkr.pagefetcher.persistence

import java.time.{Instant, OffsetDateTime, ZoneId}

import net.fwbrasil.activate.entity.Entity
import nl.dekkr.pagefetcher.model.PageUrl
import nl.dekkr.pagefetcher.persistence.persistenceContext._

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
  implicit def offsetDateTimeToLong(offsetDateTime: OffsetDateTime): Long = offsetDateTime.toEpochSecond

  implicit def longToOffsetDateTime(l: Long): OffsetDateTime = OffsetDateTime.ofInstant(Instant.ofEpochSecond(l), ZoneId.systemDefault())

}

object Storage {

  def getFromCache(request: PageUrl): Option[PageCache] = {
    // TODO observe maxAge
    transactional {
      val res2 = query {
        (entity: PageCache) =>
          where(entity.uri :== request.url) select entity orderBy {
            if (request.raw.getOrElse(false))
              entity.raw desc
            else
              entity.raw asc
          } limit 1
      }

      //val res2 = select[PageCache] where(_.uri :== request.url, _.raw :== request.raw)
      if (res2.isEmpty)
        None
      else
        Some(res2.head)
    }
  }

  def storeInCache(url: String, content: Option[String], raw: Boolean): Unit = {
    transactional {
      new PageCache(uri = url, content = content, raw = raw)
    }
  }


}