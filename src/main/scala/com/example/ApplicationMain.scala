package com.example

import java.util.UUID

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.example.api.ApiActor
import com.example.domain.actors.{Kitchen, Checkout}
import com.example.domain.messages.Order
import com.example.monitoring.Monitoring
import com.example.websocket.{WebSocket, WebSocketServiceActor}
import spray.can.Http
import spray.can.server.UHttp

import scala.concurrent.duration._

object ApplicationMain extends App {
  implicit val system = ActorSystem("McBurgerSystem")

  Monitoring(system)
  // create and start our service actor
  val service = system.actorOf(Props(classOf[ApiActor]), "api")

  val checkout = system.actorOf(Props(classOf[Checkout], service), "checkout")
  val kitchen = system.actorOf(Props(classOf[Kitchen], checkout), "kitchen")
  implicit val timeout = Timeout(5.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
  kitchen ! Order(UUID.randomUUID(), 1,1,1,1,1,1)
  readLine("Hit ENTER to exit ...\n")
  system.shutdown()
  system.awaitTermination()
}
