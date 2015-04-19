package com.example.domain.actors

import java.util.UUID

import akka.actor.Props
import akka.testkit.{TestActorRef, TestProbe}
import com.example.domain.messages._
import testUtils.ActorSpec
import scala.None


class CheckoutSpec extends ActorSpec {
  "The checkout desk" should {
    val receiverProbe = TestProbe()
    val checkout = TestActorRef(new Checkout(receiverProbe.ref))
    val uuid = UUID.randomUUID() 
    val order = Order(uuid,1,2,3,4,5,6) 
    val getStatus = GetOrderStatus(uuid)

    "show order status" in {
      checkout ! getStatus
      expectMsg(OrderStatus(None))
    }

    "aggregate orders" in {
      checkout ! order
      checkout ! getStatus
      expectMsg(OrderStatus(Some(Order(uuid,0,0,0,0,0,0))))
    }

    "tracks order completion status" in {
      def doStatusCheck[ProductReady <: OrderMessage](
          event: (UUID => ProductReady), 
          expectedState: Order) = {

        checkout ! event(uuid)
        checkout ! GetOrderStatus(uuid)
        expectMsg(OrderStatus(Some(expectedState)))
      }

      checkout ! order

      doStatusCheck(SandwichReady, 
        Order(
          uuid, 
          sandwiches = 1,
          fries= 0,
          salads = 0,
          drinks = 0,
          coffees = 0,
          shakes = 0))
     
      doStatusCheck(FriesReady, 
        Order(
          uuid, 
          sandwiches = 1,
          fries= 1,
          salads = 0,
          drinks = 0,
          coffees = 0,
          shakes = 0))
      
      doStatusCheck(SaladReady, 
        Order(
          uuid, 
          sandwiches = 1,
          fries= 1,
          salads = 1,
          drinks = 0,
          coffees = 0,
          shakes = 0))

      doStatusCheck(DrinkReady,
        Order(
          uuid, 
          sandwiches = 1,
          fries= 1,
          salads = 1,
          drinks = 1,
          coffees = 0,
          shakes = 0))

      doStatusCheck(CoffeeReady, 
        Order(
          uuid, 
          sandwiches = 1,
          fries= 1,
          salads = 1,
          drinks = 1,
          coffees = 1,
          shakes = 0))

      doStatusCheck(ShakeReady, 
        Order(
          uuid, 
          sandwiches = 1,
          fries= 1,
          salads = 1,
          drinks = 1,
          coffees = 1,
          shakes = 1))
    }
  }
}
