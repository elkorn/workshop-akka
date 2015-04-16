package com.example.domain.actors

import java.util.UUID

import akka.actor.{ActorSystem, Props}
import testUtils.ActorSpec

import scala.concurrent.duration.FiniteDuration

class DelayerSpec extends ActorSpec(ActorSystem("DelayerSpecSystem")) {
  "A Delayer actor" must {
    val delay = com.example.config.McBurger.operationalDelay

    "respond to messages with a delay" in {
      val delayer = system.actorOf(Props[Delayer])
      delayer ! Delayer.DelayRequest
      expectNoMsg(FiniteDuration(delay.toNanos, scala.concurrent.duration.NANOSECONDS))
      expectMsg(Delayer.DelayResponse)
    }
  }
}
