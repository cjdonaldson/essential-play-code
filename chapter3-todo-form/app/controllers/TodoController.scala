package controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.twirl.api.Html
import models._

import play.api.data.Form._
import play.api.data.Forms._
import play.api.data._
import play.api.data.validation.Constraint
// import play.api.data.validation.Constraints
import play.api.data.validation.Constraints._ //min
//import play.api.data.validation.Constraints._ //min

import scala.util.matching.Regex

object TodoController extends Controller with TodoDataHelpers {
  // TODO: Create a Form[Todo]:
  //  - build the basic form mapping;
  //  - create constraint to ensure the label is non-empty.
  val uuidRegex: Regex =
    "(?i:[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12})".r

  val uuidConstraint: Constraint[String] =
    pattern(regex = uuidRegex, name = "UUID", error = "error.uuid")

  val todoForm: Form[Todo] = Form(mapping(
    "id"       -> optional(text.verifying(uuidConstraint)),
    "label"    -> nonEmptyText,
    "complete" -> boolean
  )(Todo.apply)(Todo.unapply))

  def index = Action { request =>
    Ok(renderTodoList(todoList, todoForm))
  }

  def submitTodoForm = Action { implicit request =>
    // TODO: Write code to handle the form submission:
    //  - validate the form;
    //  - if form is valid:
    //     - add todo to todoList;
    //     - redirect to index;
    //  - else:
    //     - display errors.
    todoForm.bindFromRequest().fold(
      hasErrors = { errorForm =>
        BadRequest(renderTodoList(todoList, errorForm))
      },
      success = { todo =>
        todoList = todoList.addOrUpdate(todo)
        Redirect(routes.TodoController.index)
      }
    )
  }

  def renderTodoList(todoList: TodoList, form: Form[Todo]): Html =
    // TODO: Modify template to show form:
    views.html.todoList(todoList, form)
}

trait TodoDataHelpers {
  var todoList = TodoList(Seq(
    Todo("Dishes", true),
    Todo("Laundry"),
    Todo("World Domination")
  ))
}
