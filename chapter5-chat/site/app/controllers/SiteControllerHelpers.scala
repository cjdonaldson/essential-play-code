package controllers

import play.api.mvc._
import scala.concurrent.{ Future, ExecutionContext }

trait SiteControllerHelpers extends ControllerHelpers {
  implicit class RequestCookieOps(request: Request[AnyContent]) {
    def sessionCookieId: Option[String] =
      request.headers.get("ChatAuth")
  }

  implicit class ResultCookieOps(result: Result) {
    def withSessionsAuth(sessionId: String) =
      result.withHeaders("ChatAuth" -> sessionId)
  }
}
