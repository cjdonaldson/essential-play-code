package controllers

import play.api.mvc._
import play.api.libs.json._

import services._
import AuthServiceMessages._

trait ControllerHelpers extends Results {
  implicit class RequestCookieOps(request: Request[AnyContent]) {
    def jsonAs[A](implicit reads: Reads[A]): JsResult[A] =
      request.body.asJson match {
        case Some(json) => Json.fromJson[A](json)
        case None       => JsError(JsPath, "No JSON specified")
      }

    def sessionCookieId: Option[String] =
      request.cookies.get("ChatAuth").map(_.value)
  }

  implicit class ResultCookieOps(result: Result) {
    def withSessionCookie(sessionId: String) =
      result.withCookies(Cookie("ChatAuth", sessionId))
  }

  // REST API helpers - removes some nested boilerplate
  implicit class RequestJsonApiOps(request: Request[AnyContent]) {
    def withRequestJsonAs[A: Reads](f: A => Result): Result =
      request.body.asJson match {
        case Some(json) =>
          Json.fromJson[A](json) match {
            case JsSuccess(req, path) => f(req)
            case err: JsError => BadRequest(ErrorJson(err))
          }
        case None => BadRequest(ErrorJson("no json"))
      }

    def withJsonAuthentication(f: Credentials => Result): Result = {
      request.headers.get("ChatAuth") match {
        case Some(id) =>
          AuthService.whoami(id) match {
            case c: Credentials => f(c).withHeaders("ChatAuth" -> id)
            case _ => Unauthorized(ErrorJson("Expired session"))
          }
        case _ => Unauthorized(ErrorJson("No session"))
      }
    }
  }

}
