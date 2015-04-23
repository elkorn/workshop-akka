package com.example.api

import java.util.UUID

import akka.actor.Actor
import com.example.api.protocols.JsonProtocol
import com.example.domain.actors.{Aggregator, OrderReady}
import com.example.messages.TrackOrder
import spray.routing.RequestContext

class OrderStatusDispatcher extends Actor with Aggregator[UUID, RequestContext] {
  import JsonProtocol._

  def receive = {
    case TrackOrder(order, request) =>
      push(order.orderId, request)

    case OrderReady(order) if has(order.orderId) =>
      pop(order.orderId).complete(order)
  }
}