package controllers

import play.api.Logger
import play.api.Play.current
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.libs.ws._
import scala.concurrent.{ Future, ExecutionContext }

object ChatController extends Controller with SiteControllerHelpers {
  import services.AuthServiceMessages._
  import services.ChatServiceMessages._

  val chatClient = new clients.ChatServiceClient

  def chatForm = Form(mapping(
    "text" -> nonEmptyText
  )(ChatRequest.apply)(ChatRequest.unapply))

  def index(sessionId: String) = Action.async { implicit request =>
    chatRoom(sessionId)
  }

  def submitMessage(sessionId: String) = Action.async { implicit request =>
    chatForm.bindFromRequest().fold(
      hasErrors = { form: Form[ChatRequest] =>
        chatRoom(sessionId, form)
      },
      success = { chatReq: ChatRequest =>
        chatClient.chat(sessionId, chatReq) map {
          case res: ChatSuccess      => redirectToIndex(sessionId)
          case res: ChatUnauthorized => redirectToLogin
        }
      }
    )
  }

  private def chatRoom(sessionId: String, form: Form[ChatRequest] = chatForm): Future[Result] =
    chatClient.messages(sessionId) flatMap {
      case res: MessagesSuccess      =>
        Future.successful { Ok(views.html.chatroom(sessionId, res.messages, form)).withSessionsAuth(sessionId) }
      case res: MessagesUnauthorized => Future.successful(redirectToLogin)
    }

  private def redirectToIndex(sessionId: String): Result =
    Redirect(routes.ChatController.index(sessionId))

  private val redirectToLogin: Result =
    Redirect(routes.AuthController.login)
}
