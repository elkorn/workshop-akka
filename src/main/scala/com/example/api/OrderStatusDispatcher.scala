package com.example.api

import java.util.UUID

import akka.actor.Actor
import com.example.domain.actors.{Aggregator, OrderReady}
import com.example.domain.messages.TrackOrder
import spray.routing.RequestContext

class OrderStatusDispatcher extends Actor with Aggregator[UUID, RequestContext] {

  import com.example.api.protocols.JsonProtocol._

  def receive = {
    case TrackOrder(order, request) =>
      push(order.orderId, request)

    case OrderReady(order) if has(order.orderId) =>
      pop(order.orderId).complete(order)
  }
}