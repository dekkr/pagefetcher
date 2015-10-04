package nl.dekkr.pagefetcher.services

import java.time.OffsetDateTime

import com.typesafe.scalalogging.Logger
import nl.dekkr.pagefetcher.model.PageUrl
import nl.dekkr.pagefetcher.persistence.Tables._
import nl.dekkr.pagefetcher.persistence.{PageCache, Tables}
import org.slf4j.LoggerFactory
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

object StorageService {

  private val logger = Logger(LoggerFactory.getLogger("[Page storage]"))

  import dc.driver.api._

  def read(request: PageUrl): Future[Option[PageCache]] = {
    val notBefore: Long = request.maxAge match {
      case Some(minutes) =>
        logger.info(s"read  [url: ${request.url}] [age: $minutes minutes]")
        OffsetDateTime.now().minusMinutes(minutes).toEpochSecond
      case None =>
        logger.info(s"read  [url: ${request.url}] [age: unspecified]")
        0
    }
    val pageList = Tables.pages.filter(page => page.uri === request.url && page.raw === request.raw && page.created > notBefore).sortBy(_.created.desc).take(1)
    dc.db.run(pageList.result) map {
      case rows if rows.nonEmpty =>
        val row = rows.head
        Some(PageCache(uri = row.uri, content = row.content, raw = row.raw))
      case _ => None
    }
  }

  def write(url: String, content: Option[String], raw: Boolean) = {
    logger.info(s"write [url: $url] [raw: $raw]")
    dc.db.run(pages += Page(None, url, content, raw, OffsetDateTime.now().toEpochSecond))
  }

  def deleteOlderThan(nrOfHours: Int): Future[Int] = {
    val olderThan = OffsetDateTime.now().minusHours(nrOfHours).toEpochSecond
    dc.db.run(oldPagesCompiled(olderThan).delete)
  }

  private def oldPages(maxAge: Rep[Long]) = Tables.pages.filter(_.created < maxAge)

  private val oldPagesCompiled = Compiled(oldPages _)

  def initStorage = {
    dc.db.run(MTable.getTables) map {
      case tables: Vector[MTable] if tables.toList.exists(n => n.name.name == Tables.pageTableName) =>
        logger.info(s"Table ${Tables.pageTableName} exists")
      case tables =>
        logger.info(s"Creating table ${Tables.pageTableName}")
        dc.db.run(pages.schema.create)
    }
  }
}
