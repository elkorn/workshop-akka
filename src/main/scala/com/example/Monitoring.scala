package com.example

import akka.actor.{Actor, ActorRef, Terminated}
import com.example.websocket.WebSocket
import spray.http._

class Monitoring extends Actor {
  val listeners: scala.collection.mutable.Set[ActorRef] = scala.collection.mutable.Set()

  def receive = {
    case WebSocket.Open(_, origin) =>
      listeners add origin
      context watch origin

    case Terminated(origin) =>
      listeners remove origin
  }
}