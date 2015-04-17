package nl.dekkr.pagefetcher

import org.specs2.mutable.Specification
import spray.routing.Rejected
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._

class FrontendServiceSpec extends Specification with Specs2RouteTest with FrontendService {
  def actorRefFactory = system

  "FrontendService" should {

    "show a default index page" in {
      Get() ~> myRoute ~> check {
        responseAs[String] must contain("Ready to serve")
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/do_not_respond") ~> myRoute ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put() ~> sealRoute(myRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: GET"
      }
    }

    "fail on a non-existing host " in {
      Get("/v1/page?url=http://dekkr.nl.invalid")  ~> myRoute ~> check {
        status === NotFound
        responseAs[String] must contain("Host not found")
      }
    }

    "successfully fetch a page " in {
      Get("/v1/page?url=http://google.com")  ~> myRoute ~> check {
        responseAs[String] must contain("google")
      }
    }

    "fail on a non-existing page " in {
      Get("/v1/page?url=http://www.google.com/abcedf")  ~> myRoute ~> check {
        status === BadRequest
        responseAs[String] must contain("404: Not Found")
      }
    }

  }
}