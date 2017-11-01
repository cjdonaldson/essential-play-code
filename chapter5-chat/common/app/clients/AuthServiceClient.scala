package clients

import play.api._
import play.api.libs.json._
import play.api.libs.ws._
import scala.concurrent.{ Future, ExecutionContext }

class AuthServiceClient(implicit application: Application, context: ExecutionContext) {
  import services.AuthServiceMessages._

  // TODO: Call the login endpoint of the auth service,
  // located at http://localhost:9002/login
  def login(req: LoginRequest): Future[LoginResponse] =
    url("login")
      .post(Json.toJson(req))
      .flatMap { parseResponse[LoginResponse](_) }

  // TODO: Call the whoami endpoint of the auth service,
  // located at http://localhost:9002/whoami
  //
  // Remember to set the `Authorization` header in the request
  // to the `sessionId` provided
  def whoami(sessionId: String): Future[WhoamiResponse] = {
    urlWithAuth("whoami", sessionId)
      .get()
      .flatMap { parseResponse[WhoamiResponse](_) }
  }

  def urlWithAuth(endpoint: String, sessionId: String) =
    url(s"$endpoint/$sessionId")
      .withHeaders("ChatAuth" -> sessionId)

  def url(endpoint: String) =
    WS.url(s"http://localhost:9002/$endpoint")
      .withFollowRedirects(true)
      .withRequestTimeout(5000)

  def parseResponse[A: Reads](response: WSResponse): Future[A] = {
    Json.fromJson[A](response.json) match {
      case JsSuccess(value, _) => Future.successful(value)
      case error: JsError => Future.failed(InvalidResponseException(response, error))
    }
  }

  case class InvalidResponseException(response: WSResponse, jsError: JsError) extends
    Exception(s"BAD API response:\n${response.json}\n$jsError")
}
