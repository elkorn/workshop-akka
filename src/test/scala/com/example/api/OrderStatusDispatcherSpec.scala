package com.example.api
//
//import java.util.UUID
//
//import akka.actor.Props
//import akka.testkit.TestActorRef
//import com.example.domain.actors.OrderReady
//import com.example.domain.messages.{TrackOrder, Order}
//import org.mockito.Mockito
//import org.scalatest.Matchers
//import org.scalatest.mock.MockitoSugar
//import spray.httpx.marshalling.ToResponseMarshaller
//import spray.routing.RequestContext
//import testUtils.ActorSpec
//import org.mockito.Mockito._


//class OrderStatusDispatcherSpec extends ActorSpec with MockitoSugar with Matchers  {
  // TODO what's up with the any matcher?
//  "OrderStatusDispatcher" should {
//    "Dispatch a response to an order request" in {
//      val actor = TestActorRef(Props[OrderStatusDispatcher])
//      val request = mock[RequestContext]
//      val order = Order(UUID.randomUUID(), 1,1,1,1,1,1)
//      implicit val marshaller = any(ToResponseMarshaller[Order].getClass)
//
//      actor ! TrackOrder(order, request)
//      actor ! OrderReady(order)
//
//      verify(request).complete(order)
//    }
//  }
//}
