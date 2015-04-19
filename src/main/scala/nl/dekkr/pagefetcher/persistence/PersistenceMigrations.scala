package nl.dekkr.pagefetcher.persistence

import net.fwbrasil.activate.migration.Migration
import nl.dekkr.pagefetcher.persistence.persistenceContext._

class PersistenceMigrations extends Migration {

  def timestamp = 201518041211l

  def up {
    table[PageCache]
      .createTable(
        _.column[String]("uri"),
        _.column[String]("content"),
        _.column[Boolean]("raw"),
        _.column[Long]("createdAt"))

  }

}