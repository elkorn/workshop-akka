package com.example

import akka.actor.SupervisorStrategy.{Resume, Escalate, Restart}
import akka.actor._
import akka.io.IO
import akka.pattern.ask
import akka.routing.{RoundRobinPool, Broadcast, FromConfig}
import akka.util.Timeout
import com.example.api.{ApiActor, OrderStatusDispatcher}
import com.example.domain.actors.{Checkout, Kitchen}
import com.example.domain.messages.KickTheBucket
import spray.can.Http

import scala.concurrent.duration._

object ApplicationMain extends App {
  implicit val system = ActorSystem("McBurgerSystem")

  implicit val timeout = Timeout(5.seconds)
  // create and start our service actor
  val statusDispatcher = system.actorOf(Props[OrderStatusDispatcher], "status-dispatcher")
  val checkout = system.actorOf(Props(classOf[Checkout], statusDispatcher), "checkout")

  private val restartStrategy: OneForOneStrategy = OneForOneStrategy() {
    case _ => Restart
  }

  // This escalates the error to the supervisor. Since the supervisor of the kitchen is the McBurgerSystem guardian, the whole actor system will go down.
  private val escalateStrategy: OneForOneStrategy = OneForOneStrategy() {
    case _ => Escalate
  }

  private val resumeStrategy: OneForOneStrategy = OneForOneStrategy() {
    case _ => Resume
  }

  val kitchen = system.actorOf(RoundRobinPool(3, supervisorStrategy = resumeStrategy).props(routeeProps = Props(classOf[Kitchen], checkout)), "kitchen")

  def startApi(): Unit = {
    val service = system.actorOf(Props(classOf[ApiActor], statusDispatcher, kitchen), "api")
    IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
  }

  // Stop all routees and then the router.
//  kitchen ! PoisonPill

  // Stop all routees.
//  kitchen ! Kill

  kitchen ! KickTheBucket

//    startApi()
}
