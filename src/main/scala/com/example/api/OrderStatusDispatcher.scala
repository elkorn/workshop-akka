package com.example.api

import java.util.UUID

import akka.actor.{ActorRef, Actor}
import akka.event.LoggingReceive
import com.example.domain.actors.OrderReady
import com.example.domain.messages.TrackOrder

class OrderStatusDispatcher extends Actor {

  def dispatch(
    readyEvent: OrderReady,
    sender: ActorRef) =
    sender ! readyEvent

  def waitForTrackingRequest = LoggingReceive {
    case TrackOrder(order) =>
      context.become(waitForTrackingResponse(order.orderId, sender()))
  }

  def waitForTrackingResponse(
    orderId: UUID,
    requestSender: ActorRef) =
    LoggingReceive {
      case x: OrderReady if x.order.orderId == orderId =>
        dispatch(x, requestSender)
        context.stop(self)
    }

  def receive = waitForTrackingRequest
}