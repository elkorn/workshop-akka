package com.example

import akka.actor.Actor

trait ComposableActor extends Actor {
  private var receives: List[Actor.Receive] = List()

  protected def registerReceive(receive: Actor.Receive) {
    receives = receive :: receives
  }

  def receive = receives reduce {_ orElse _}
}