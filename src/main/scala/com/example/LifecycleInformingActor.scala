package com.example

import akka.actor.Actor
import com.example.LifecycleInformingActor.{Dead, Online, LifecycleMessage}

object LifecycleInformingActor {
  object LifecycleEventType extends Enumeration {
    type EventType = Value
    val Dead, Online = Value
  }

  trait LifecycleMessage {
    val domainName: String
    val refName: String
    val address: String
    val eventType: LifecycleEventType.Value
  }

  case class Online(domainName: String, refName: String, address: String) extends LifecycleMessage {
    val eventType = LifecycleEventType.Online
  }

  case class Dead(domainName: String, refName: String, address: String) extends LifecycleMessage {
    val eventType = LifecycleEventType.Dead
  }
}

abstract class LifecycleInformingActor(val domainName: String) extends Actor {
  private def publish[Msg <: LifecycleMessage](msg: (String, String, String) => Msg): Unit = {
    context.system.eventStream.publish(msg(domainName, self.path.name, self.path.address.toString))
  }

  override def preStart() =
    publish(Online)

  override def postStop() =
    publish(Dead)
}
