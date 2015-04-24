package com.example.domain.actors

import java.util.UUID

import akka.actor.{Actor, ActorRef, Props}
import akka.event.LoggingReceive
import com.example.domain.messages.{Order, PrepareProduct}

trait KitchenWorkers {
  _: Actor =>
  val checkoutDesk: ActorRef
  def delayer(name: String): ActorRef =
    context.actorOf(
      Props[Delayer],
      name)
  // TODO [WORKSHOP] Use the names of the worker classes that you have defined.
  val sandwich = context.actorOf(
    Props(
      classOf[YourSandwichCreatorTypeHere],
      delayer("SandwichMachine"),
      checkoutDesk),
    "Sandwich")
  val fries = context.actorOf(
    Props(
      classOf[YourFriesCreatorTypeHere],
      delayer("FriesMachine"),
      checkoutDesk),
    "Fries")
  val salad = context.actorOf(
    Props(
      classOf[YourSaladCreatorTypeHere],
      delayer("SaladMachine"),
      checkoutDesk),
    "Salad")
  val coffee = context.actorOf(
    Props(
      classOf[YourCoffeeCreatorTypeHere],
      delayer("CoffeeMachine"),
      checkoutDesk),
    "Coffee")
  val shake = context.actorOf(
    Props(classOf[YourShakeCreatorTypeHere],
      delayer("ShakeMachine"),
      checkoutDesk),
    "Shake")
  val drink = context.actorOf(
    Props(
      classOf[YourDrinkCreatorTypeHere],
      delayer("DrinkMachine"),
      checkoutDesk),
    "Drink")
}

class Kitchen(val checkoutDesk: ActorRef) extends Actor with KitchenWorkers {
  def receive = LoggingReceive {
    case order: Order => {
      /*
        Appending '_' to the function execution allows treating it as a partial fn.
        `prepare` is of type (ActorRef, Int) => Unit.
       */
      val prepare = requestPreparationOfProducts(order.orderId) _
      // TODO [WORKSHOP] tell your minions to make the products! The checkout desk also needs to know that the new order has been accepted, sicne it will be receiving all the sandwiches etc. and packing them into colorful boxes.
    }
  }

  private def requestPreparationOfProducts(orderId: UUID)
    (
      worker: ActorRef,
      howMany: Int) =
    (1 to howMany).foreach((x) => worker ! PrepareProduct(orderId))
}
