package com.example.websocket

import akka.actor.{ActorRef, Actor, ActorLogging, Props}
import spray.can.Http

object WebSocketServiceActor {
  def props(webSocketRoute: WebSocket.Route[ActorRef], httpListenerActor: ActorRef): Props =
    Props(classOf[WebSocketServiceActor], webSocketRoute, httpListenerActor)
}

class WebSocketServiceActor(webSocketRoute: WebSocket.Route[ActorRef], httpListenerActor: ActorRef) extends Actor with ActorLogging {
  def receive = {
    // when a new connection comes in we register a WebSocketConnection actor as the per connection handler
    case Http.Connected(remoteAddress, localAddress) =>
      val serverConnection = sender()
      val conn = context.actorOf(WebSocketServiceWorker.props(serverConnection, webSocketRoute, httpListenerActor))
      serverConnection ! Http.Register(conn)
  }
}
