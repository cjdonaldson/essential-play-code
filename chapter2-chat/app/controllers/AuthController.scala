package controllers

import play.api._
import play.api.mvc._

class AuthController extends Controller {
  import services.AuthService
  import services.AuthServiceMessages._

  // TODO: Complete:
  //  - Call AuthService.login
  //     - If it's LoginSuccess, return an Ok response that sets a cookie
  //     - If it's UserNotFound or PasswordIncorrect, return a BadRequest response
  //
  // NOTE: We don't know how to create HTML yet,
  // so populate each response with a plain text message.
  def login(username: Username, password: Password) = Action { request =>
    AuthService.login(LoginRequest(username, password)) match {
      case LoginSuccess(x) => Ok("Logged in").withCookies(Cookie("ChatAuth", username))

      case _ => BadRequest("User not found or password incorrect")
    }
  }
}
