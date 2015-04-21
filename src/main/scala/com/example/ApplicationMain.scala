package com.example

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.example.api.ApiActor
import com.example.websocket.{WebSocket, WebSocketServiceActor}
import spray.can.Http

import scala.concurrent.duration._

object ApplicationMain extends App {
  implicit val system = ActorSystem("McBurgerSystem")

  lazy val monitoring = system.actorOf(Props[Monitoring], "monitoring")
  // create and start our service actor
  lazy val service = system.actorOf(Props(classOf[ApiActor], monitoring), "api")
  lazy val websocketWithApi = system.actorOf(WebSocketServiceActor.props(WebSocket.Routes(("/status" -> monitoring)), service), "service")
  implicit val timeout = Timeout(5.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(websocketWithApi, interface = "localhost", port = 8080)
  readLine("Hit ENTER to exit ...\n")
  system.shutdown()
  system.awaitTermination()
}
