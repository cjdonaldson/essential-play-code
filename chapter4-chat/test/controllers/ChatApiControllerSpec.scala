package controllers

import services.ChatService
import services.ChatServiceMessages._
import org.scalatestplus.play._
import play.api.libs.json._

class ChatApiControllerSpec extends PlaySpec with ControllerSpecHelpers {
  // TODO: Complete:
  //  - Call the login API endpoint
  //  - Extract and return the sessionId
  def sessionId: String = {
    val response = await(wsCall(routes.AuthApiController.login).post(
      Json.obj("username" -> "alice", "password" -> "password1")
    ))

    response.status must equal(200)

    val json = response.json
    val auth = response.header("ChatAuth")
    (json \ "type") must equal (JsString("LoginSuccess"))
    auth.isDefined must equal (true)
    auth.get
  }

  "chat endpoint" must {
    // TODO: Complete:
    //  - Log in using the `sessionId` method above
    //  - Call the chat endpoint with the correct session header
    //  - Check the response contains a valid message
    "post a message" in {
      sessionId.isEmpty must equal (false)
      val response = await(wsCall(routes.ChatApiController.chat)
        .withHeaders("ChatAuth" -> sessionId)
        .post( Json.toJson(Message("alice", "first message")) )
      )

      response.status must equal(200)
      response.body must equal (Json.toJson(Message("alice", "first message")).toString)
    }
  }

  "messages endpoint" must {
    // TODO: Complete:
    //  - Create a few messages directly using `ChatService`
    //  - Log in using the `sessionId` method above
    //  - Call the messages endpoint with the correct session header
    //  - Check the response contains the messages you created
    "list messages" in {
      sessionId.isEmpty must equal (false)
      val author = "alice"
      val text1 = "line 1"
      chat(sessionId, author, text1)

      val response1 = await(wsCall(routes.ChatApiController.messages)
        .withHeaders("ChatAuth" -> sessionId)
        .get
      )

      response1.status must equal(200)
      val msgs1 = MessagesSuccess(Seq(Message(author, text1)))
      response1.body must equal (Json.toJson(msgs1).toString)

      val text2 = "line 2"
      chat(sessionId, author, text2)

      val response2 = await(wsCall(routes.ChatApiController.messages)
        .withHeaders("ChatAuth" -> sessionId)
        .get
      )

      response2.status must equal(200)
      val msgs2 = MessagesSuccess(Seq(Message(author, text1), Message(author, text2)))
      response2.body must equal (Json.toJson(msgs2).toString)

    }
  }

  private def chat(sessionId: String, author: String, text: String) {
    val msg = Message(author, text)
    val json = Json.toJson(msg)
    val response1 = await(wsCall(routes.ChatApiController.chat)
      .withHeaders("ChatAuth" -> sessionId)
      .post(json)
    )

    response1.status must equal(200)
    response1.body must equal (json.toString)
  }
}