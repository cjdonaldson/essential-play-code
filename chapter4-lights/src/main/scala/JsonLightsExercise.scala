// import java.awt.Color
// import java.util.Date
import play.api.data.validation.ValidationError
import play.api.libs.json._
// import play.api.libs.functional.syntax._

object JsonLightsExercise {
  sealed trait TrafficLight
  final case object Red extends TrafficLight
  final case object Amber extends TrafficLight
  final case object Green extends TrafficLight

  private final val redNumber   = BigDecimal(0.0)
  private final val amberNumber = BigDecimal(1.0)
  private final val greenNumber = BigDecimal(2.0)

  // TODO: Complete:
  //  - Define a JSON format for `TrafficLight`:
  //     - Red is serialized as the number 0
  //     - Amber is serializes as the number 1
  //     - Green is serialized as the number 2
  implicit object trafficLightFormat extends Format[TrafficLight] {
    def writes(light: TrafficLight): JsValue = light match {
      case Red   => JsNumber(redNumber)
      case Amber => JsNumber(amberNumber)
      case Green => JsNumber(greenNumber)
    }

    def reads(json: JsValue): JsResult[TrafficLight] = json match {
      case JsNumber(value) if value == redNumber   => JsSuccess(Red)
      case JsNumber(value) if value == amberNumber => JsSuccess(Amber)
      case JsNumber(value) if value == greenNumber => JsSuccess(Green)
      case other    => JsError(ValidationError("error.expected.trafficlight"))
    }
  }
}
