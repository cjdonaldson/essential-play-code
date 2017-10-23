package controllers

import play.api._
import play.api.mvc._

class ChatController extends Controller {
  import services.AuthService
  import services.AuthServiceMessages._

  import services.ChatService
  import services.ChatServiceMessages._

  private def withAuthenticatedUser(request: Request[AnyContent])(f: Credentials => Result): Result = {
    val badRequest = BadRequest("Not logged in")
    request.cookies.get("ChatAuth").fold(badRequest){ cookie =>
      AuthService.whoami(cookie.value) match {
        case credentials @ Credentials(id, user) => f(credentials)
        case _ => badRequest
      }
    }
  }

  // TODO: Complete:
  //  - Check if the user is logged in
  //     - If they are, return an Ok response containing a list of messages
  //     - If they aren't, redirect to the login page
  //
  // NOTE: We don't know how to create HTML yet,
  // so populate each response with a plain text message.
  def index = Action { request =>
    withAuthenticatedUser(request){ credentials => Ok(ChatService.messages.mkString("\n")) }
  }

  // TODO: Complete:
  //  - Check if the user is logged in
  //     - If they are, create a message from the relevant author
  //     - If they aren't, redirect to the login page
  //
  // NOTE: We don't know how to create HTML yet,
  // so populate each response with a plain text message.
  def submitMessage(text: String) = Action { request =>
    withAuthenticatedUser(request){ credentials =>
      Ok(ChatService.chat(credentials.username, text).toString)
      Redirect(routes.ChatController.index)
    }
  }
}
