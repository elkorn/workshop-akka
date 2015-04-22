package com.example

import akka.actor._
import akka.testkit.{TestActorRef, TestProbe}
import com.example.LifecycleInformingActor.{Dead, Online}
import testUtils.ActorSpec

/**
 * Created by elkorn on 4/21/15.
 */
class LifecycleInformingActorSpec extends ActorSpec {

  private case class NameEvent(className: String)


  "LifecycleInformingActor" should {
    "know its className" in {
      class TempTestActor(watcher: ActorRef) extends LifecycleInformingActor("TempTestActor") {
        watcher ! NameEvent(domainName)

        def receive = {
          case _ => {}
        }
      }

      val watcher = TestProbe()
      TestActorRef(new TempTestActor(watcher.ref))
      watcher.expectMsg(NameEvent("TempTestActor"))
    }

    "know its ref name" in {
      class TempTestActor(watcher: ActorRef) extends LifecycleInformingActor("TempTestActor") {
        watcher ! NameEvent(self.path.name)

        def receive = {
          case _ => {}
        }
      }

      val watcher = TestProbe()
      TestActorRef(new TempTestActor(watcher.ref), "TestRefName")
      watcher.expectMsg(NameEvent("TestRefName"))
    }

    "publish system event stream notice on startup" in {
      val domainName = "TempTestActor"
      val refName: String = "TestRefName2"

      case class TempTestActor(watcher: ActorRef) extends LifecycleInformingActor(domainName) {
        def receive = {
          case _ => {}
        }
      }
      val watcher = TestProbe()
      system.eventStream.subscribe(testActor, classOf[Online])
      val actor = new TempTestActor(watcher.ref)
      TestActorRef(actor, refName)
      expectMsg(Online(domainName, refName, actor.self.path.address.toString))
      system.eventStream.unsubscribe(testActor, classOf[Online])
    }

    "publish system event stream notice after stopping" in {
      val domainName = "TempTestActor"
      val refName: String = "TestRefName3"

      case class TempTestActor(watcher: ActorRef) extends LifecycleInformingActor(domainName) {
        def receive = {
          case _ => {}
        }
      }

      val watcher = TestProbe()
      system.eventStream.subscribe(testActor, classOf[Dead])
      val actor = TestActorRef[TempTestActor](Props(classOf[TempTestActor], watcher.ref), refName)

      actor ! PoisonPill

      expectMsg(Dead(domainName, refName, actor.underlyingActor.self.path.address.toString))
    }
  }
}
