package services

object ChatService {
  import ChatServiceMessages._

  private var postedMessages = Vector[Message]()

  // TODO: Complete:
  //  - Delete all messages in `postedMessages`
  def clear(): Unit = {
    postedMessages = Vector()
  }

  // TODO: Complete:
  //  - Return a list of messages in `postedMessages`
  def messages: Seq[Message] = {
    postedMessages.toList
  }

  // TODO: Complete:
  //  - Add a new message to `postedMessages`
  def chat(author: String, text: String): Message = {
    val m = Message(author, text)
    postedMessages = postedMessages :+ m
    m
  }
}
