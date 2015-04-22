package com.example.monitoring

import akka.actor._
import akka.event.LoggingReceive
import com.example.monitoring.LifecycleInformingActor.{Dead, Online}

object Monitoring {
  def apply(system: ActorSystem) = {
    val monitoringRef = system.actorOf(Props[MonitoringActor])
    system.eventStream.subscribe(monitoringRef, classOf[Dead])
    system.eventStream.subscribe(monitoringRef, classOf[Online])
  }

  private class MonitoringActor extends Actor  {
    def receive = LoggingReceive {
      case x => println(x.toString())
    }
  }
}

