package com.example.domain.actors

import java.util.UUID

import akka.actor.{Actor, ActorRef}
import akka.pattern.ask
import akka.util.Timeout
import com.example.config.McBurger
import com.example.domain.messages._

import scala.concurrent.duration
import scala.util.Success

private[actors] class KitchenWorker[ProductPrepared <: ProductReadyEvent](
                                                          createResponse: (UUID) => ProductPrepared,
                                                          workExecutor: ActorRef,
                                                          productReceiver: ActorRef)
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

class Fries(workExecutor: ActorRef, productReceiver: ActorRef) 
  extends KitchenWorker(FriesReady, workExecutor, productReceiver)
class Coffee(workExecutor: ActorRef, productReceiver: ActorRef) 
  extends KitchenWorker(CoffeeReady, workExecutor, productReceiver)
class Drink(workExecutor: ActorRef, productReceiver: ActorRef) 
  extends KitchenWorker(DrinkReady, workExecutor, productReceiver)
class Salad(workExecutor: ActorRef, productReceiver: ActorRef) 
  extends KitchenWorker(SaladReady, workExecutor, productReceiver)
class Sandwich(workExecutor: ActorRef, productReceiver: ActorRef) 
  extends KitchenWorker(SandwichReady, workExecutor, productReceiver)
class Shake(workExecutor: ActorRef, productReceiver: ActorRef) 
  extends KitchenWorker(ShakeReady, workExecutor, productReceiver)
