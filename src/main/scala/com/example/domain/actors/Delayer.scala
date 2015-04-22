package com.example.domain.actors

import akka.actor.{Actor, ActorLogging}

import com.example.config.{McBurger => config}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

private[actors] object Delayer {
  case object DelayRequest
  case object DelayResponse

  val delayDuration = config.operationalDelay
}

private[actors] class Delayer extends Actor with ActorLogging {
  import Delayer._

  implicit val ec: ExecutionContext = context.dispatcher

  def receive = {
    case DelayRequest => {
      val originalSender = sender()
      context.system.scheduler.scheduleOnce(500.milliseconds) {
        originalSender ! DelayResponse
      }
    }
  }
}
