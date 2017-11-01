package clients

import play.api._
import play.api.libs.json._
import play.api.libs.ws._
import scala.concurrent.{ Future, ExecutionContext }

class ChatServiceClient(implicit application: Application, context: ExecutionContext) {
  import services.ChatServiceMessages._

  // TODO: Call the messages endpoint of the chat service,
  // located at http://localhost:9001/messages
  //
  // Remember to set the `Authorization` header in the request
  // to the `sessionId` provided
  def messages(sessionId: String): Future[MessagesResponse] =
    urlWithAuth("message", sessionId)
      .get()
      .flatMap(parseResponse[MessagesResponse](_))

  // TODO: Call the messages endpoint of the chat service,
  // located at http://localhost:9001/chat
  //
  // Remember to set the `Authorization` header in the request
  // to the `sessionId` provided
  def chat(sessionId: String, chatReq: ChatRequest): Future[ChatResponse] =
    urlWithAuth("message", sessionId)
      .post(Json.toJson(chatReq))
      .flatMap(parseResponse[ChatResponse](_))

  def urlWithAuth(endpoint: String, sessionId: String) =
    WS.url(s"http://localhost:9001/$endpoint")
      .withFollowRedirects(true)
      .withRequestTimeout(5000)
      .withHeaders("ChatAuth" -> sessionId)

  def parseResponse[A: Reads](response: WSResponse): Future[A] = {
    Json.fromJson[A](response.json) match {
      case JsSuccess(value, _) => Future.successful(value)
      case error: JsError => Future.failed(InvalidResponseException(response, error))
    }
  }

  case class InvalidResponseException(response: WSResponse, jsError: JsError) extends
    Exception(s"BAD API response:\n${response.json}\n$jsError")
}
