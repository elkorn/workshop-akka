package com.example.domain.actors

import akka.actor.{Actor, ActorLogging}
import com.example.config.{McBurger => config}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.Random

private[actors] object Delayer {
  private val delayVariance = config.operationalDelayVariance.toMillis

  val delayDuration = FiniteDuration(
    config.operationalDelay.toMillis - delayVariance + Random.nextInt(2 * delayVariance.toInt),
    concurrent.duration.MILLISECONDS)

  case object DelayRequest

  case object DelayResponse

}

private[actors] class Delayer extends Actor with ActorLogging {

  import com.example.domain.actors.Delayer._

  implicit val ec: ExecutionContext = context.dispatcher

  def receive = {
    case DelayRequest => {
      val originalSender = sender()
      context.system.scheduler.scheduleOnce(delayDuration) {
        originalSender ! DelayResponse
      }
    }
  }
}
