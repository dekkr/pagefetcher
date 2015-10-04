package nl.dekkr.pagefetcher.messages

case class StorePage(url: String, content: Option[String], raw: Boolean)
