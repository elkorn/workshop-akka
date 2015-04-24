package com.example.domain.actors

import java.util.UUID

import akka.actor.{ActorRef, Props, Actor}
import akka.event.LoggingReceive
import com.example.domain.messages._
import scala.Option

case class GetOrderStatus(orderId: UUID)
case class OrderStatus(order: Option[Order])
case class OrderReady(order: Order)

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

  def handleReady[Product <: ProductReadyEvent](product: Product, orderStatus: Map[UUID, Order]): Map[UUID, Order] = {
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
    // TODO [WORKSHOP] Wire this up! See the CheckoutSpec for guidance. This actor should respond to the following messages: GetOrderStatus, Order and ProductReadyEvent (see com.example.domain.messages)
    case _ => {}
  }
}
