package nl.dekkr.pagefetcher

import nl.dekkr.pagefetcher.model.PageUrl
import org.specs2.mutable.Specification

class ModelSpec extends Specification {

  "Model" should {

    "fail on a empty url " in {
      PageUrl(url = "") must throwAn[Exception]
    }

    "fail on a invalid protocol in the  url " in {
      PageUrl(url = "file:///root.html") must throwAn[Exception]
    }

    "fail on a invalid query in the  url " in {
      PageUrl(url = "http://www.something.invalid/page.html?age=45&size= blue") must throwAn[Exception]
    }

    "accept a valid url " in {
      PageUrl(url = "http://dekkr.nl/page.html?age=45&size=blue") must throwAn[Exception].not
    }
  }

}
