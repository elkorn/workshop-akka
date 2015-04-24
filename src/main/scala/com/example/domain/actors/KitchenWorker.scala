package com.example.domain.actors

import java.util.UUID

import akka.actor.SupervisorStrategy.Escalate
import akka.actor.{Actor, ActorRef}
import akka.event.LoggingReceive
import akka.pattern.ask
import akka.util.Timeout
import com.example.config.McBurger
import com.example.domain.messages._

import scala.concurrent.duration
import scala.util.{Failure, Success}

private[actors] class KitchenWorker[ProductPrepared <: ProductReadyEvent](
                                                          createResponse: (UUID) => ProductPrepared,
                                                          workExecutor: ActorRef,
                                                          productReceiver: ActorRef) extends Actor {

  import context.dispatcher

  def receive = LoggingReceive {
    case PrepareProduct(orderId) => {
      implicit val timeout = Timeout(McBurger.operationalDelay.toNanos * 2, duration.NANOSECONDS)
      (workExecutor ? Delayer.DelayRequest).onComplete {
        case Success(_) => productReceiver ! createResponse(orderId)
        case Failure(_) => Escalate
      }
    }
  }
}

// TODO [WORKSHOP] Define classes that extends the KitchenWorker trait so that there is somebody to prepare Sandwiches, Fries, Salads, Coffees, Drinks and Shakes. ;)
// HINT: Check com.example.domain.messages.KitchenEvents to see what you're dealing with.