package com.example.domain.actors

import java.util.UUID

import akka.actor.SupervisorStrategy.Escalate
import akka.actor.{Actor, ActorRef}
import akka.event.LoggingReceive
import akka.pattern.ask
import akka.util.Timeout
import com.example.config.McBurger
import com.example.domain.messages._
import com.example.monitoring.LifecycleInformingActor

import scala.concurrent.duration
import scala.util.{Failure, Success}

private[actors] class KitchenWorker[ProductPrepared <: ProductReadyEvent](
                                                          createResponse: (UUID) => ProductPrepared,
                                                          workExecutor: ActorRef,
                                                          productReceiver: ActorRef,
                                                          domainName: String = "")
  extends LifecycleInformingActor(domainName) {
  import context.dispatcher

  def receive = LoggingReceive {
    case PrepareProduct(orderId) => {
      implicit val timeout = Timeout(McBurger.operationalDelay.toNanos * 2, duration.NANOSECONDS)
      val originalSender = sender()
      (workExecutor ? Delayer.DelayRequest).onComplete {
        case Success(_) => productReceiver ! createResponse(orderId)
        case Failure(_) => Escalate
      }
    }
  }
}

class Fries(workExecutor: ActorRef, productReceiver: ActorRef) 
  extends KitchenWorker(FriesReady, workExecutor, productReceiver, "Fries")
class Coffee(workExecutor: ActorRef, productReceiver: ActorRef)
  extends KitchenWorker(CoffeeReady, workExecutor, productReceiver, "Coffee")
class Drink(workExecutor: ActorRef, productReceiver: ActorRef) 
  extends KitchenWorker(DrinkReady, workExecutor, productReceiver, "Drink")
class Salad(workExecutor: ActorRef, productReceiver: ActorRef) 
  extends KitchenWorker(SaladReady, workExecutor, productReceiver, "Salad")
class Sandwich(workExecutor: ActorRef, productReceiver: ActorRef) 
  extends KitchenWorker(SandwichReady, workExecutor, productReceiver, "Sandwich")
class Shake(workExecutor: ActorRef, productReceiver: ActorRef) 
  extends KitchenWorker(ShakeReady, workExecutor, productReceiver, "Shake")
