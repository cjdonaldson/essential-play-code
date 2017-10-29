import play.api.libs.json._
// import play.api.libs.functional.syntax._

import play.api.data.validation.ValidationError

object JsonAnimalsExercise {
  sealed trait Animal
  final case class Dog(name: String) extends Animal
  final case class Insect(legs: Int) extends Animal
  final case class Swallow(maxLoad: Int) extends Animal

  // TODO: Complete:
  //  - Create a JSON format for Animal by:
  //     - Creating JSON formats for Dog, Insect, and Swallow
  //     - Creating a JSON format for Animal that adds/uses a
  //       "type" field to distinguish between the three cases
  val dogFormat = Json.format[Dog]
  val insectFormat = Json.format[Insect]
  val swallowFormat = Json.format[Swallow]

  implicit object animalFormat extends Format[Animal] {
    def writes(animal: Animal): JsValue = animal match {
      case a: Dog => dogFormat.writes(a) ++ Json.obj("type" -> "Dog")
      case a: Insect => insectFormat.writes(a) ++ Json.obj("type" -> "Insect")
      case a: Swallow => swallowFormat.writes(a) ++ Json.obj("type" -> "Swallow")
    }
    def reads(j: JsValue): JsResult[Animal] = (j \ "type") match {
      case JsString("Dog") => dogFormat.reads(j)
      case JsString("Insect") => insectFormat.reads(j)
      case JsString("Swallow") => swallowFormat.reads(j)
      case other => JsError(JsPath \ "type", ValidationError("error.expected.animal.type"))
    }

  }
}