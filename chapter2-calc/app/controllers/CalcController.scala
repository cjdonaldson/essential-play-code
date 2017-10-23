package controllers

import play.api._
import play.api.mvc._

object CalcController extends Controller {
  // TODO: Create an action called `add`:
  //
  //  - accept two integers extracted from the URL;
  //  - add them together;
  //  - return a plain text HTTP 200 response containing the result.
  def add(a: Int, b: Int) = Action { request =>
    Ok(s"result of $a + $b is ${a + b}\n")
  }

  // TODO: Create an action called `add`:
  //
  //  - accept two booleans extracted from the URL;
  //  - and them together;
  //  - return a plain text HTTP 200 response containing the result.
  def and(a: Boolean, b: Boolean) = Action { request =>
    Ok(s"result of $a and $b is ${a && b}\n")
  }

  // TODO: Create an action called `concat`:
  //
  //  - accept a rest argument extracted from the URL;
  //  - concatenate the URL-decoded path fragments from the argument,
  //    effectively removing slashes from the text;
  //  - return a plain text HTTP 200 response containing the result.
  //
  // TIP: Use the `urlDecode` helper method if you need to to decode the .
  def concat(args: String) = Action { request =>
    val res = urlDecode(args.replace("/",""))
    Ok(s"result of concat '$args' is '$res'\n")
  }

  // TODO: Create an action called `concat`:
  //
  //  - accept a list of integers extracted from the URL;
  //  - sort the list;
  //  - return a space separated plain text HTTP 200 response of the result.
  def sort(a: List[Int], b: List[String]) = Action { request =>
    Ok(s"result of sort $a and $b is ${(a.map(_.toString) ++ b).sorted.mkString(" ")}\n")
  }
  def sortN(a: List[Int]) = Action { request =>
    Ok(s"result of sort $a is ${a.sorted.mkString(" ")}\n")
  }
  def sortS(a: List[String]) = Action { request =>
    Ok(s"result of sort $a is ${a.sorted.mkString(" ")}\n")
  }


  // TODO: Create an action called `howToAdd`:
  //
  //  - accept two integers extracted from the URL;
  //  - return a plain text HTTP 200 response containing the
  //    HTTP method and URL required to add them together.
  //
  // TIP: Use the reverse route for `add()` to construct the URL.
  def howToAdd(a: Int, b: Int) = Action { request =>
    val call = routes.CalcController.add(a, b)
    Ok(s"result howToAdd $a $b '${call.url}'\n")
  }

  private def urlDecode(str: String) =
    java.net.URLDecoder.decode(str, "UTF-8")
}
