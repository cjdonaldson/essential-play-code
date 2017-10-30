package controllers

import org.scalatestplus.play._
import play.api.libs.json._
import play.api.test._

import services.AuthServiceMessages._

class AuthApiControllerSpec extends PlaySpec with ControllerSpecHelpers {
  "login endpoint" must {
    "recognise known usernames" in {
      // TODO: Complete:
      //  - Replace with a call to the login endpoint:
      val response = await(wsCall(routes.AuthApiController.login).post(
        Json.toJson(LoginRequest("alice", "password1"))
      ))

      response.status must equal(200)

      // TODO: Complete:
      //  - Replace with one or more tests of the JSON
      // Json.fromJson[AuthServiceMessages.LoginRequest](response.json) must equal("sss")
      val json = response.json
      val auth = response.header("ChatAuth")
      (json \ "type") must equal (JsString("LoginSuccess"))
      auth.isDefined must equal (true)
    }

    // TODO: Complete
    //  - Call the login endpoint with a bad username
    //  - Check that the result is a JSON-encoded UserNotFound message
    "not recognise unknown usernames" in {
      val response = await(wsCall(routes.AuthApiController.login).post(
        Json.toJson(LoginRequest("al", "password1"))
      ))

      response.status must equal(400)

      val json = response.json
      val auth = response.header("ChatAuth")
      (json \ "type") must equal (JsString("UserNotFound"))
      (json \ "username") must equal (JsString("al"))
      auth.isDefined must equal (false)
    }

    // TODO: Complete
    //  - Call the login endpoint with a bad password
    //  - Check that the result is a JSON-encoded PasswordIncorrect message
    "not recognise invalid passwords" in {
      val response = await(wsCall(routes.AuthApiController.login).post(
        Json.toJson(LoginRequest("alice", "bad-password"))
      ))

      response.status must equal(400)

      val json = response.json
      val auth = response.header("ChatAuth")
      (json \ "type") must equal (JsString("PasswordIncorrect"))
      (json \ "username") must equal (JsString("alice"))
      auth.isDefined must equal (false)
    }
  }

  "whoami endpoint" must {
    // TODO: Complete
    //  - Call the login endpoint with a valid username and password
    //  - Retrieve the sessionId from the response
    //  - Pass the sessionId to the whoami endpoint
    //  - Check the result is a a JSON-encoded Credentials message
    "recognise sessionIds from login endpoint" in {
      val response = await(wsCall(routes.AuthApiController.login).post(
        Json.toJson(LoginRequest("alice", "password1"))
      ))

      response.status must equal(200)

      val json = response.json
      (json \ "type") must equal (JsString("LoginSuccess"))
      val id: String = (json \ "sessionId").as[String]

      val response2 = await(
        wsCall(routes.AuthApiController.whoami)
        .withHeaders("ChatAuth" -> id)
        .get
      )
      response2.status must equal(200)
      response2.body must equal (Json.toJson(Credentials(id, "alice")).toString)
    }
  }
}