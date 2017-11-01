package controllers

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.ws._
import scala.concurrent.Future

import services._

trait ControllerHelpers extends Results {
  implicit class RequestJsonOps(request: Request[AnyContent]) {
    def jsonAs[A](implicit reads: Reads[A]): JsResult[A] =
      request.body.asJson match {
        case Some(json) => Json.fromJson[A](json)
        case None       => JsError(JsPath, "No JSON specified")
      }

    def withAuthorization(f: String => Result): Result = {
      request.headers.get("ChatAuth") match {
        case Some(id) => f(id).withHeaders("ChatAuth" -> id)
        case _ => Unauthorized(ErrorJson("No session"))
      }
    }
  }
}
