package com.example.domain.actors

import java.util.UUID

import akka.actor.Props
import akka.testkit.TestProbe
import com.example.domain.messages.{PrepareProduct, ProductReadyEvent}
import testUtils.ActorSpec

class KitchenWorkerSpec extends ActorSpec {
  case class TestProductPreparedMessage(orderId: UUID) extends ProductReadyEvent
  "A kitchen worker" should {
    val uuid = UUID.randomUUID()
    "ask the underlying work executor for results" in {
        val probe = TestProbe()
        val worker = system.actorOf(
          Props(classOf[KitchenWorker[TestProductPreparedMessage]],
            TestProductPreparedMessage,
            probe.ref,
            TestProbe().ref))

        worker ! PrepareProduct(uuid)

        probe.expectMsg(Delayer.DelayRequest)
        probe.reply(Delayer.DelayResponse)
        expectMsg(TestProductPreparedMessage(uuid))
    }
  }
}
