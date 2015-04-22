package com.example.api

import akka.actor.{Actor, ActorLogging, Props}
import com.example.ActorPerRequestCreator
import com.example.domain.messages.{TrackOrder, Order}
import spray.routing.Route

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor

class ApiActor extends Actor with Service with ActorLogging with ActorPerRequestCreator {
  import com.example.api.protocols.JsonProtocol._

  def trackOrderStatus(message: ApiMessage): Route =
    ctx => actorPerRequest(ctx, Props(new OrderStatusDispatcher()), message)

  val myRoute = pathPrefix("order") {
    post { request =>
      entity(as[Order]) { order =>
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
  def receive = runRoute(serviceRoute) orElse runRoute(myRoute)
}
