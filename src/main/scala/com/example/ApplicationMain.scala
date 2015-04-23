package com.example

import akka.actor.{Kill, ActorSystem, PoisonPill, Props}
import akka.io.IO
import akka.pattern.ask
import akka.routing.{Broadcast, FromConfig}
import akka.util.Timeout
import com.example.api.{ApiActor, OrderStatusDispatcher}
import com.example.domain.actors.{Checkout, Kitchen}
import spray.can.Http

import scala.concurrent.duration._

object ApplicationMain extends App {
  implicit val system = ActorSystem("McBurgerSystem")

  implicit val timeout = Timeout(5.seconds)
  // create and start our service actor
  val statusDispatcher = system.actorOf(Props[OrderStatusDispatcher], "status-dispatcher")
  val checkout = system.actorOf(Props(classOf[Checkout], statusDispatcher), "checkout")
  val kitchen = system.actorOf(FromConfig.props(Props(classOf[Kitchen], checkout)), "kitchen-roundrobin")

  def startApi(): Unit = {
    val service = system.actorOf(Props(classOf[ApiActor], statusDispatcher, kitchen), "api")
    IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
  }

  println("=== SENDING POISON PILL ===")
  // Stop all routees and then the router.
//  kitchen ! PoisonPill

  // Stop all routees.
  kitchen ! Broadcast(Kill)

  //  startApi()
}
