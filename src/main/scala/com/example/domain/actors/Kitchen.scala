package com.example.domain.actors

import java.util.UUID

import akka.actor.{Actor, ActorRef, Props}
import akka.event.LoggingReceive
import com.example.domain.messages.{Order, PrepareProduct}

trait KitchenWorkers {
  _: Actor =>
  val checkoutDesk: ActorRef
  val sandwich = context.actorOf(
    Props(
      classOf[Sandwich],
      context.actorOf(
        Props[Delayer],
        "SandwichMachine"),
      checkoutDesk),
    "Sandwich")
  val fries = context.actorOf(
    Props(
      classOf[Fries],
      context.actorOf(
        Props[Delayer],
        "FriesMachine"),
      checkoutDesk),
    "Fries")
  val salad = context.actorOf(
    Props(
      classOf[Salad],
      context.actorOf(
        Props[Delayer],
        "SaladMachine"),
      checkoutDesk),
    "Salad")
  val coffee = context.actorOf(
    Props(
      classOf[Coffee],
      context.actorOf(
        Props[Delayer],
        "CoffeeMachine"),
      checkoutDesk),
    "Coffee")
  val shake = context.actorOf(
    Props(classOf[Shake],
      context.actorOf(
        Props[Delayer],
        "ShakeMachine"),
      checkoutDesk),
    "Shake")
  val drink = context.actorOf(
    Props(
      classOf[Drink],
      context.actorOf(
        Props[Delayer],
        "DrinkMachine"),
      checkoutDesk),
    "Drink")
}

class Kitchen(val checkoutDesk: ActorRef) extends Actor with KitchenWorkers {
  def receive = LoggingReceive {
    case order: Order => {
      checkoutDesk ! order
      /*
        Appending '_' to the function execution allows treating it as a partial fn.
       */
      val prepare = requestPreparationOfProducts(order.orderId) _
      prepare(sandwich, order.sandwiches)
      prepare(fries, order.fries)
      prepare(salad, order.salads)
      prepare(coffee, order.coffees)
      prepare(shake, order.shakes)
      prepare(drink, order.drinks)
    }
  }

  private def requestPreparationOfProducts(orderId: UUID)
    (
      worker: ActorRef,
      howMany: Int) =
    (1 to howMany).foreach((x) => worker ! PrepareProduct(orderId))
}
