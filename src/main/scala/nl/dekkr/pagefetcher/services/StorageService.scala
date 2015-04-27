package nl.dekkr.pagefetcher.services

import java.time.OffsetDateTime

import com.typesafe.scalalogging.Logger
import nl.dekkr.pagefetcher.model.PageUrl
import nl.dekkr.pagefetcher.persistence.PageCache
import nl.dekkr.pagefetcher.persistence.persistenceContext._
import org.slf4j.LoggerFactory

import scala.language.postfixOps

object StorageService {

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
        logger.debug(s"miss  [url: ${request.url}] [raw: ${request.raw.getOrElse(false)}]")
        None
      } else {
        logger.debug(s"hit   [url: ${res2.head.uri}] [raw: ${res2.head.raw}] [age: ${(OffsetDateTime.now().toEpochSecond - res2.head.createdAt) / 60} minutes]")
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

  def deleteOlderThan(nrOfHours: Int): Unit = {
    val olderThan = OffsetDateTime.now().minusHours(nrOfHours).toEpochSecond
    transactional {
      delete {
        (entity: PageCache) => where(entity.createdAt :< olderThan)
      }
    }
  }

}
