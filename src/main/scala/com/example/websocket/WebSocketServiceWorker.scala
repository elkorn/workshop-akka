package com.example.websocket

import akka.actor._
import spray.can
import spray.can.websocket
import spray.can.websocket.FrameCommandFailed
import spray.can.websocket.frame.{BinaryFrame, TextFrame}
import spray.http.HttpRequest

object WebSocketServiceWorker {
  def props(serverConnection: ActorRef,
    webSocketRoute: WebSocket.Route[ActorRef],
    httpListenerActor: ActorRef) =
    Props(classOf[WebSocketServiceWorker], serverConnection, webSocketRoute, httpListenerActor)
}

class WebSocketServiceWorker(
  val serverConnection: ActorRef,
  webSocketRoute: WebSocket.Route[ActorRef],
  httpListenerActor: ActorRef)
  extends spray.routing.HttpServiceActor with websocket.WebSocketServerWorker {

  var path: String = _
  var target: ActorRef = _

  override def receive = handshakeOnRoute orElse handshaking orElse businessLogicNoUpgrade orElse closeLogic

  def handshakeOnRoute: Receive = {
    case msg @ websocket.HandshakeRequest(state) => {
      state match {
        case failure: websocket.HandshakeFailure => handshaking(msg)
        case context: websocket.HandshakeContext => {
          val path: String = context.request.uri.path.toString()
          webSocketRoute.lift(path) match {
            case Some(requestActor) => {
              target = requestActor
              handshaking(msg)
            }

            case None => throw new Exception(s"WS not supported on path $path")
          }
        }
      }
    }
  }

  def businessLogic: Receive = {
    case can.websocket.UpgradedToWebSocket =>
      target ! WebSocket.Open(path, serverConnection)

    case TextFrame(bytes) =>
      target forward WebSocket.Pull(path, bytes.decodeString("utf-8"))

    case BinaryFrame(bytes) =>
      target forward WebSocket.Pull(path, bytes.decodeString("utf-8"))

    case WebSocket.Push(msg) => send(TextFrame(msg))

    case msg: FrameCommandFailed =>
      log.error("frame command failed", msg)

    case msg: HttpRequest => httpListenerActor forward msg
  }

  def businessLogicNoUpgrade: Receive = {
    case msg => httpListenerActor forward msg
  }
}