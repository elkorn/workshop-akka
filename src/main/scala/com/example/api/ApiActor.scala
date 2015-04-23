package com.example.api

import akka.actor.{Actor, ActorLogging, Props}
import com.example.ActorPerRequestCreator
import com.example.domain.messages.{Order, TrackOrder}
import com.example.messages.OrderRequest
import spray.http.StatusCodes
import spray.routing._

class ApiActor extends Actor with Service with ActorLogging with ActorPerRequestCreator {

  import com.example.api.protocols.JsonProtocol._

  def trackOrderStatus(message: ApiMessage): Route =
    ctx => actorPerRequest(ctx, Props(new OrderStatusDispatcher()), message)


  implicit val rejectionHandler = RejectionHandler {
    case x :: _ => complete(StatusCodes.BadRequest, x toString)
  }

  implicit val exceptionHandler = ExceptionHandler {
    case x => complete(StatusCodes.InternalServerError, x toString)
  }

  val myRoute = path("order") {
        post { request =>
          entity(as[OrderRequest]) { orderRequest =>
            println("Got entity")
            val order = Order.fromRequest(orderRequest)

            context.actorSelection("/user/kitchen") ! order

            trackOrderStatus {
              TrackOrder(order)
            }
          }
        }
  }

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}
