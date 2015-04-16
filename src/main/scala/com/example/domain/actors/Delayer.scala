package com.example.domain.actors

import akka.actor.{Actor, ActorLogging}

import com.example.config.{McBurger => config}

private[actors] object Delayer {
  case object DelayRequest
  case object DelayResponse

  val delayDuration = config.operationalDelay
}

private[actors] class Delayer extends Actor with ActorLogging {
  import Delayer._

  def receive = {
    case DelayRequest => {
      Thread.sleep(delayDuration.toMillis)
      sender() ! DelayResponse
    }
  }
}
