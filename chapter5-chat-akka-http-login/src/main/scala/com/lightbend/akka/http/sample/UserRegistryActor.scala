package usr.cjdinc.sample

import akka.actor.{ Actor, ActorLogging, Props }
import java.util.UUID

//#user-case-classes
// include isAdmin: Boolean
final case class User(name: String, password: String, age: Int, countryOfResidence: String)
final case class Users(users: Seq[User])
//#user-case-classes

final case class Login(user: String, password: String)
final case class Session(sessionId: String, user: String)

object UserRegistryActor {
  // Protocol messages
  final case class ActionPerformed(description: String)
  final case object GetUsers
  final case class CreateUser(user: User)
  final case class GetUser(name: String)
  final case class DeleteUser(name: String)

  final case class UserLogin(login: Login)
  final case class UserCredentials(sessionId: String, user: String)
  final case class WhoAmI(sessionId: String)
  final case class Logout(sessionId: String)

  def props: Props = Props[UserRegistryActor]
}

class UserRegistryActor extends Actor with ActorLogging {
  import UserRegistryActor._

  //var users = Set.empty[User]
  var users = Set(
      User("alice","password1", 30, "USA"),
      User("bob","password2", 31, "can" ),
      User("chuck","password3", 51, "usa" ),
    )

  var sessions = Map.empty[String, String]

  def receive: Receive = {
    case GetUsers =>
      sender() ! Users(users.toSeq)
    case CreateUser(user) =>
      users += user
      sender() ! ActionPerformed(s"User ${user.name} created.")
    case GetUser(name) =>
      sender() ! users.find(_.name == name)
    case DeleteUser(name) =>
      users.find(_.name == name) foreach { user => users -= user }
      sender() ! ActionPerformed(s"User ${name} deleted.")
    case UserLogin(login) =>
      val userMaybe = users.find(u => u.name == login.user && u.password == login.password)
      val sessionMaybe = userMaybe.fold(None: Option[Session]){ u =>
        val session = Session(UUID.randomUUID.toString, u.name)
        sessions += (session.sessionId -> session.user)
        // log.debug(s"Login state $sessions")
        Option(session)
      }
      sender() ! sessionMaybe
    case WhoAmI(sessionId) =>
      // log.debug(s"Who Am I state $sessionId from $sessions")
      val sessionMaybe = sessions.get(sessionId)
      val userMaybe = sessionMaybe.fold(None: Option[User]){ s =>
        users.find(u => u.name == s)
      }
      sender() ! userMaybe
    case Logout(sessionId) =>
      // log.debug(s"Logout state $sessionId from $sessions")
      val userMaybe = sessions.get(sessionId)
      val result = userMaybe.fold("Unknown session - noone to log out"){ s =>
        sessions -= sessionId
        s"User $userMaybe logged out."
      }
      sender() ! ActionPerformed(result)
  }
}
