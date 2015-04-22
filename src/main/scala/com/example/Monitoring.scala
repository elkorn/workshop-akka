package com.example

import akka.actor.{ActorLogging, Actor, ActorRef, Terminated}
import com.example.websocket.{WebSocketProducerActor, WebSocket}

class Monitoring extends Actor with ActorLogging {
  def receive = {
    case _ => {}
  }
}