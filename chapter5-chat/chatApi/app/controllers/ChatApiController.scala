package controllers

import play.api.Logger
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws._
import play.api.Play.current
import scala.concurrent.{ Future, ExecutionContext }

import services.ChatService

object ChatApiController extends Controller with ControllerHelpers {
  import services.AuthServiceMessages._
  import services.ChatServiceMessages._

  val authClient = new clients.AuthServiceClient

  def messages = Action.async { request =>
    // TODO: Complete:
    //  - Authenticate the user using `authClient`
    //  - Return a list of messages from `ChatService`
    withJsonAuthentication(request) { credentials =>
      Ok(Json.toJson(MessagesSuccess(ChatService.messages)))
    }
  }

  def chat = Action.async { request =>
    // TODO: Complete:
    //  - Authenticate the user using `authClient`
    //  - Extract a `ChatRequest` from `request`
    //  - Post the `ChatRequest` to `ChatService`
    //  - Return the resulting `Message`
    withJsonAuthentication(request) { credentials =>
      withRequestJsonAs[ChatRequest](request) { chatRequest =>
        val msg = ChatService.chat(credentials.username, chatRequest.text)
        Ok(Json.toJson(ChatSuccess(msg)))
      }
    }
  }

  def withRequestJsonAs[A: Reads](request: Request[AnyContent])(f: A => Result): Result =
    request.body.asJson match {
      case Some(json) =>
        Json.fromJson[A](json) match {
          case JsSuccess(req, path) => f(req)
          case err: JsError => BadRequest(ErrorJson(err))
        }
      case None => BadRequest(ErrorJson("no json"))
    }

  def withJsonAuthentication(request: Request[AnyContent])(f: Credentials => Result): Future[Result] =
      request.headers.get("ChatAuth") match {
        case Some(id) =>
          authClient.whoami(id) flatMap {
            case c: Credentials => Future { f(c) }
            case _ => Future { Unauthorized(ErrorJson("Expired session")) }
          }
        case _ => Future { Unauthorized(ErrorJson("No session")) }
      }
}
