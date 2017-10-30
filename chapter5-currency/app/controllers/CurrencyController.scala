package controllers

import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models._

object CurrencyController extends Controller with ExchangeRateHelpers {
  def convertOne(fromAmount: Double, fromCurrency: Currency, toCurrency: Currency) =
    Action.async { request =>
      // TODO:
      //  - Convert fromAmount to USD using ExchangeRateHelpers.toUSD
      //  - Convert the USD amount to toCurrency using ExchangeRateHelpers.fromUSD
      //  - Format the result using formatConversion
      for {
        usd <- toUSD(fromAmount, fromCurrency)
        to <- fromUSD(usd, toCurrency)
      } yield Ok(formatConversion(fromAmount, fromCurrency, to, toCurrency))
    }

  def convertAll(fromAmount: Double, fromCurrency: Currency) =
    Action.async { request =>
      // TODO:
      //  - For all toCurrency in ExchangeRateHelpers.currencies:
      //     - Convert fromAmount to USD using ExchangeRateHelpers.toUSD
      //     - Convert the USD amount to toCurrency using ExchangeRateHelpers.fromUSD
      //     - Format the result using formatConversion
      //  - Combine all results into a single plain text response
      import scala.concurrent.Await
      import scala.concurrent.duration._
      import scala.language.postfixOps
      toUSD(fromAmount, fromCurrency) map { usd =>
        val all = currencies map { c =>
          fromUSD(usd, c) map { other =>
            formatConversion(fromAmount, fromCurrency, other, c)
          }
        }
        val s = Future.sequence(all) map (res => Ok(res.mkString("\n")))
        Await.result(s, 10 seconds)
      }
    }
}

trait ExchangeRateHelpers {
  val currencies: Seq[Currency] =
    Seq(USD, GBP, EUR)

  def toUSD(amount: Double, from: Currency): Future[Double] =
    from match {
      case USD => Future.successful(amount * 1.0)
      case GBP => Future.successful(amount * 1.5)
      case EUR => Future.successful(amount * 1.1)
    }

  def fromUSD(amount: Double, to: Currency): Future[Double] =
    toUSD(1.0, to) map (amount / _)

  def formatConversion(fromAmount: Double, fromCurrency: Currency, toAmount: Double, toCurrency: Currency): String =
    Currency.format(fromAmount, fromCurrency) + " = " + Currency.format(toAmount, toCurrency)
}
