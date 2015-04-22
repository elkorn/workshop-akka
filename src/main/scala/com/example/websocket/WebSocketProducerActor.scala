package com.example.websocket

import akka.actor.{Actor, ActorRef}
import spray.can.websocket.FrameCommand
import spray.can.websocket.frame.TextFrame

trait WebSocketProducerActor {
  _: Actor =>
  def push(
    target: ActorRef,
    message: String) {
    target ! FrameCommand(TextFrame(message))
  }
}