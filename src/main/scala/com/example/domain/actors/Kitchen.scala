package com.example.domain.actors

import java.util.UUID

import akka.actor.{ActorRef, Props, Actor}
import com.example.domain.messages.{PrepareProduct, Order}

class Kitchen extends Actor {
  lazy val sandwich = context.actorOf(Props[Sandwich])
  lazy val fries = context.actorOf(Props[Fries])
  lazy val salad = context.actorOf(Props[Salad])
  lazy val coffee = context.actorOf(Props[Coffee])
  lazy val shake = context.actorOf(Props[Shake])
  lazy val drink = context.actorOf(Props[Drink])

  private def requestPreparationOfProducts(orderId: UUID)(worker: ActorRef, howMany: Int) =
    (1 to howMany).foreach((x) => worker ! PrepareProduct(orderId))

  def receive = {
    case order : Order => {
      /*
        Appending '_' to the function execution allows treating it as a partial fn.
        The most basic way to achieve the same result would be to define it as

          def requestPreparationOfProducts(orderId: UUID): (ActorRef, Int) => Unit =  {
            (worker, howMany) => {
              ...
            }
          }
       */
      val prepare = requestPreparationOfProducts(order.orderId)_
      prepare(sandwich, order.sandwiches)
      prepare(fries, order.fries)
      prepare(salad, order.salads)
      prepare(coffee, order.coffees)
      prepare(shake, order.shakes)
      prepare(drink, order.drinks)
    }
  }
}
