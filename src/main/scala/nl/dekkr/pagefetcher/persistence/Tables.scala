package nl.dekkr.pagefetcher.persistence

object Tables extends DbConf {

  import dc.driver.api._

  val pageTableName: String = "stored_page"

  case class Page(id: Option[Int], uri: String, content: Option[String], raw: Boolean, created: Long)

  class Pages(tag: Tag) extends Table[Page](tag, pageTableName) {
    def id = column[Int]("page_id", O.PrimaryKey, O.AutoInc)

    def uri = column[String]("uri")

    def content = column[Option[String]]("content")

    def raw = column[Boolean]("is_raw")

    def created = column[Long]("created")

    def * = (id.?, uri, content, raw, created) <>(Page.tupled, Page.unapply)
  }

  val pages = TableQuery[Pages]

}