package com.example.domain.actors

import java.util.UUID

import akka.actor.{ActorRef, Props, Actor}
import akka.event.LoggingReceive
import com.example.api.ApiMessage
import com.example.domain.messages._
import scala.Option

case class GetOrderStatus(orderId: UUID)
case class OrderStatus(order: Option[Order])
case class OrderReady(order: Order) extends ApiMessage

trait Aggregator[TKey, TValue] {
  var data: Map[TKey, TValue] = Map()

  def push(key: TKey, value: TValue) =
    data = data + (key -> value)

  def peek(key: TKey): TValue =
    data(key)

  def has(key: TKey): Boolean =
    data.isDefinedAt(key)

  def pop(key: TKey): TValue = {
    val result = peek(key)
    data -= key
    result
  }
}

class Checkout(statusReceiver: ActorRef) extends Actor with Aggregator[UUID, Order] {
  var orderStatus: Map[UUID, Order]= Map()

  def isReady(order: Order): Boolean =     
    order.hasAllProducts(peek(order.orderId))

  def handleReady[Product <: OrderMessage]
      (product: Product)
      (orderStatus: Map[UUID, Order]): 
    Map[UUID, Order] = {

    if (!orderStatus.isDefinedAt(product.orderId)) orderStatus
    else {
      val order = orderStatus(product.orderId)
      product match {
        case x : SandwichReady => 
          orderStatus + (product.orderId -> order.add(sandwiches = 1))
        case x : FriesReady => 
          orderStatus + (product.orderId -> order.add(fries = 1))
        case x : SaladReady => 
          orderStatus + (product.orderId -> order.add(salads = 1))
        case x : CoffeeReady => 
          orderStatus + (product.orderId -> order.add(coffees = 1))
        case x : DrinkReady => 
          orderStatus + (product.orderId -> order.add(drinks = 1))
        case x : ShakeReady => 
          orderStatus + (product.orderId -> order.add(shakes = 1))
      }
    }
  }

  def receive = LoggingReceive {
    case GetOrderStatus(orderId) => {
      if(has(orderId))
        sender() ! OrderStatus(Some(orderStatus(orderId)))
      else 
        sender() ! OrderStatus(None)
    }

    case order: Order => {
      orderStatus = orderStatus + (order.orderId -> 
        Order(order.orderId, 0,0,0,0,0,0))
      push(order.orderId, order)
    }

    case x : OrderMessage => {
      orderStatus = handleReady(x)(orderStatus)
      val currentOrder = orderStatus(x.orderId)
      if (isReady(currentOrder)) {
        orderStatus -= x.orderId
        statusReceiver ! OrderReady(pop(x.orderId))
      }
      else 
        statusReceiver ! OrderStatus(Some(currentOrder))
    }
  }
}
