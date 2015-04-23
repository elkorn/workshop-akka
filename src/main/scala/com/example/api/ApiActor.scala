package com.example.api

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.example.api.protocols.JsonProtocol
import com.example.domain.messages.Order
import com.example.messages.{TrackOrder, OrderRequest}
import spray.http.MediaTypes._
import spray.http.StatusCodes
import spray.routing.{ExceptionHandler, RejectionHandler, Route}

class ApiActor(
  orderStatusDispatcher: ActorRef,
  kitchen: ActorRef) extends Actor with Service with ActorLogging {

  import JsonProtocol._

  val rejectionHandler = RejectionHandler {
    case x :: _ => complete(StatusCodes.BadRequest, x toString)
  }

  val exceptionHandler = ExceptionHandler {
    case x => complete(StatusCodes.InternalServerError, x toString)
  }

  val myRoute = handleRejections(rejectionHandler) {
    handleExceptions(exceptionHandler) {
      path("order") {
        post {
          entity(as[OrderRequest]) { orderRequest ⇒
            respondWithStatus(StatusCodes.OK) {
              respondWithMediaType(`application/json`) {
                issueOrder(Order.fromRequest(orderRequest))
              }
            }
          }
        }
      }
    }
  }

  def issueOrder(order: Order): Route =
    ctx => {
      kitchen ! order
      orderStatusDispatcher ! TrackOrder(order, ctx)
    }


  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(
    serviceRoute ~ myRoute
  )
}
