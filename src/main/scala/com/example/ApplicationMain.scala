package com.example

import java.util.UUID

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.routing.FromConfig
import akka.util.Timeout
import com.example.api.{OrderStatusDispatcher, ApiActor}
import com.example.domain.actors.{Kitchen, Checkout}
import com.example.domain.messages.Order
import spray.can.Http

import scala.concurrent.duration._
import scala.util.Random

object ApplicationMain extends App {
  implicit val system = ActorSystem("McBurgerSystem")

  def startApi(): Unit = {
    val service = system.actorOf(Props(classOf[ApiActor], statusDispatcher, kitchen), "api")
    IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
  }


  // create and start our service actor
  val statusDispatcher = system.actorOf(Props[OrderStatusDispatcher], "status-dispatcher")
  val checkout = system.actorOf(Props(classOf[Checkout], statusDispatcher), "checkout")

//  val kitchen = system.actorOf(Props(classOf[Kitchen], checkout), "kitchen")
  val kitchen = system.actorOf(FromConfig.props(Props(classOf[Kitchen], checkout)), "kitchen-round-robin")
  implicit val timeout = Timeout(5.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler

  (1 to 100) foreach {(_) =>
    kitchen ! Order(UUID.randomUUID(), Random.nextInt(10), 0, 0,0,0,0)
  }
  println("========= SENT MESSAGES =========")

//  startApi()
}
