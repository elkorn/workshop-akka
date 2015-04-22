package com.example

import akka.actor.{ActorLogging, Actor, ActorRef, Terminated}
import com.example.websocket.{WebSocketProducerActor, WebSocket}

class Monitoring extends Actor with WebSocketProducerActor with ActorLogging {
  val listeners: scala.collection.mutable.Set[ActorRef] = scala.collection.mutable.Set()

  def receive = {
    case WebSocket.Open(_, origin) =>
      listeners add origin
      context watch origin

    case Terminated(origin) =>
      listeners remove origin
  }
}