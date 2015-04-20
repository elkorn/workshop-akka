package com.example.domain.actors

import java.util.UUID

import akka.actor.Props
import akka.testkit.{TestActorRef, TestProbe}
import com.example.domain.messages._
import testUtils.ActorSpec
import scala.None

import org.scalatest.BeforeAndAfterEach


class CheckoutSpec extends ActorSpec with BeforeAndAfterEach {
  var receiverProbe = TestProbe()
  var checkout: TestActorRef[Checkout] = TestActorRef(new Checkout(receiverProbe.ref))

  override def beforeEach() {
    receiverProbe = TestProbe()
    checkout = TestActorRef(new Checkout(receiverProbe.ref))
  }

  "The checkout desk" should {
    "show order status" in {
      val uuid = UUID.randomUUID() 
      val getStatus = GetOrderStatus(uuid)
      checkout ! getStatus
      expectMsg(OrderStatus(None))
    }

    "aggregate orders" in {
      val uuid = UUID.randomUUID() 
      val getStatus = GetOrderStatus(uuid)
      val order = Order(uuid,1,2,3,4,5,6) 
      checkout ! order
      checkout ! getStatus
      expectMsg(OrderStatus(Some(Order(uuid,0,0,0,0,0,0))))
    }

    "track order completion status" in {
      val uuid = UUID.randomUUID() 
      val getStatus = GetOrderStatus(uuid)
      val order = Order(uuid,1,2,3,4,5,6) 

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
   
    "send order status updates" in {
      val uuid = UUID.randomUUID() 
      val order = Order(uuid,1,2,3,4,5,6) 
      checkout ! order
      checkout ! SandwichReady(uuid)
      receiverProbe.expectMsg(OrderStatus(Some(
        Order(uuid, 1, 0, 0, 0, 0, 0)
      )))
    }

    "send order completion notification" in { 
      val uuid = UUID.randomUUID() 
      val order = Order(uuid,2,2,2,2,2,2) 

      def sendN[Product <: OrderMessage](product: (UUID) => Product, id: UUID, n: Int) {
        (1 to n).foreach((_) => checkout ! product(id))
      }

      checkout ! order
      sendN(SandwichReady, uuid, 2)
      sendN(FriesReady, uuid, 2)
      sendN(SaladReady, uuid, 2)
      sendN(CoffeeReady, uuid, 2)
      sendN(DrinkReady, uuid, 2)
      sendN(ShakeReady, uuid, 2)

      receiverProbe.receiveN(11)
      receiverProbe.expectMsg(OrderReady(order))
    }
  }
}
