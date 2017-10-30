package controllers

import play.api._
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.libs.ws._
import play.api.mvc._
import play.twirl.api.Html
import scala.concurrent.{ Future, ExecutionContext }
import models._

object WeatherController extends Controller {
  def index = Action { request =>
    Ok(views.html.index(Seq(
      "Birmingham",
      "Brighton",
      "London"
    )))
  }

  // TODO: Once you have completed `fetchWeather` and `fetchLocation` below,
  // combine the results here and feed them to the template in `report.scala.html`:
  def report(location: String) =
    Action.async { request =>
      val weatherFuture = fetchWeather(location)
      val forecastFuture = fetchForecast(location)
      for {
        weather <- weatherFuture
        forecast <- forecastFuture
      } yield Ok(views.html.report(location, weather, forecast))
    }

  // TODO: Complete this method. Use the WS API to gather data from the following endpoint,
  // and parse it as an instance of `models.Weather`:
  //
  // GET http://api.openweathermap.org/data/2.5/weather?q=<location>,uk
  def fetchWeather(location: String): Future[Weather] =
    fetch[Weather]("weather", location)

  // TODO: Complete this method. Use the WS API to gather data from the following endpoint,
  // and parse it as an instance of `models.Forecast`:
  //
  // GET http://api.openweathermap.org/data/2.5/forecast?q=<location>,uk
  def fetchForecast(location: String): Future[Forecast] =
    fetch[Forecast]("forecast", location)

  private def fetch[A: Reads](endpoint: String, location: String): Future[A] =
    WS.url(s"http://api.openweathermap.org/data/2.5/$endpoint?q=$location,uk")
      .withFollowRedirects(true)
      .withRequestTimeout(5000)
      .get()
      .map (request => request.json.as[A])

}
