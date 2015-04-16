package testUtils

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.BeforeAndAfterAll

abstract class ActorSpec(_system: ActorSystem = ActorSystem()) extends TestKit(_system) with ImplicitSender with testUtils.SpecLike with BeforeAndAfterAll {
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
}