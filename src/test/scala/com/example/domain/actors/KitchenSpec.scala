package com.example.domain.actors

import java.util.UUID

import akka.actor.Props
import akka.testkit.{TestActorRef, TestProbe}
import com.example.domain.messages.{PrepareProduct, Order}
import testUtils.ActorSpec

class KitchenSpec extends ActorSpec {
  "The kitchen" should {
    "dispatch work to the kitchen according to placed orders" in {
      val order = Order(
        UUID.randomUUID(),
        sandwiches = 1,
        fries = 2,
        salads = 3,
        drinks = 4,
        coffees = 5,
        shakes = 6)
      val sandwichProbe = TestProbe()
      val friesProbe = TestProbe()
      val saladProbe = TestProbe()
      val coffeeProbe = TestProbe()
      val shakeProbe = TestProbe()
      val drinkProbe = TestProbe()
      val amount: Map[TestProbe, Int] = Map(
        sandwichProbe -> order.sandwiches,
        friesProbe -> order.fries,
        saladProbe -> order.salads,
        coffeeProbe -> order.coffees,
        shakeProbe -> order.shakes,
        drinkProbe -> order.drinks)

      val probes = Seq(sandwichProbe, friesProbe, saladProbe, coffeeProbe, shakeProbe, drinkProbe)

      val kitchen = TestActorRef(Props(new Kitchen {
        override lazy val sandwich = sandwichProbe.ref
        override lazy val fries = friesProbe.ref
        override lazy val salad = saladProbe.ref
        override lazy val coffee = coffeeProbe.ref
        override lazy val shake = shakeProbe.ref
        override lazy val drink = drinkProbe.ref
      }))

      kitchen ! order

      probes.foldLeft(Nil: Seq[AnyRef])((result, probe) => {
        result ++ probe.receiveN(amount(probe))
      }) foreach ((msg) => msg should be(PrepareProduct(order.orderId)))
    }
  }
}
