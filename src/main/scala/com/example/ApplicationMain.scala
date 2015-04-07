package com.example

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.routing.FromConfig
import akka.util.Timeout
import com.example.api.ApiActor
import spray.can.Http

import scala.concurrent.duration._

object ApplicationMain extends App {
  implicit val system = ActorSystem("McBurgerSystem")
  val master = system.actorOf(FromConfig.props(Props[Worker]), "master")

  // create and start our service actor
  val service = system.actorOf(Props[ApiActor], "demo-service")

  implicit val timeout = Timeout(5.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)

  // This example app will ping pong 3 times and thereafter terminate the ActorSystem -
  // see counter logic in PingActor
  system.awaitTermination()
}