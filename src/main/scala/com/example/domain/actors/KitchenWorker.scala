package com.example.domain.actors

import java.util.UUID

import akka.actor.{Actor, ActorRef}
import akka.pattern.ask
import akka.util.Timeout
import com.example.config.McBurger
import com.example.domain.messages._

import scala.concurrent.duration
import scala.util.Success

private[actors] class KitchenWorker[ProductPrepared <: ProductReadyEvent](createResponse: (UUID) => ProductPrepared,
                                                          workExecutor: ActorRef)
  extends Actor {
  import context.dispatcher

  def receive = {
    case PrepareProduct(orderId) => {
      implicit val timeout = Timeout(McBurger.operationalDelay.toNanos, duration.NANOSECONDS)
      val originalSender = sender()
      (workExecutor ? Delayer.DelayRequest).onComplete {
        case Success(_) => originalSender ! createResponse(orderId)
      }
    }
  }
}

class Fries(workExecutor: ActorRef) extends KitchenWorker(FriesReady, workExecutor)
class Coffee(workExecutor: ActorRef) extends KitchenWorker(CoffeeReady, workExecutor)
class Drink(workExecutor: ActorRef) extends KitchenWorker(DrinkReady, workExecutor)
class Salad(workExecutor: ActorRef) extends KitchenWorker(SaladReady, workExecutor)
class Sandwich(workExecutor: ActorRef) extends KitchenWorker(SandwichReady, workExecutor)
class Shake(workExecutor: ActorRef) extends KitchenWorker(ShakeReady, workExecutor)