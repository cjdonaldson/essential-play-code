package usr.cjdinc.sample

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import usr.cjdinc.sample.UserRegistryActor.ActionPerformed
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat4(User)
  implicit val usersJsonFormat = jsonFormat1(Users)
  implicit val loginJsonFormat = jsonFormat2(Login)
  implicit val sessionJsonFormat = jsonFormat2(Session)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
