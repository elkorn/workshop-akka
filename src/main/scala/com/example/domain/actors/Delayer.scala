package com.example.domain.actors

import akka.actor.{Actor, ActorLogging}

import scala.concurrent.duration._

import java.util.UUID

import com.example.domain.messages.IdentifiedMessage
import com.example.config.{McBurger => config}

private[domain] object Delayer {
  case class DelayRequest(id: UUID) extends IdentifiedMessage
  case class DelayResponse(id: UUID) extends IdentifiedMessage

  val delayDuration = config.operationalDelay
}

private[domain] class Delayer extends Actor with ActorLogging {
  import Delayer._

  def receive = {
    case DelayRequest(id) => {
      Thread.sleep(delayDuration.toMillis)
      sender() ! DelayResponse(id)
    }
  }
}
