//package com.example.api
//
//import akka.actor.ActorRef
//import com.example.ActorOnDemand
//import com.example.domain.messages.SystemStatus
//import spray.routing.{HttpService, Directives, RequestContext, Route}
//
//trait StatusStreamingSupport extends HttpService with Directives with ActorOnDemand {
//  val monitoringActor: ActorRef
//
//  def statusStreamingHandler(requestContext: RequestContext): Unit = {
//    createActor(() => new StatusStreamingActor {
//      val statusMonitorActor: ActorRef = monitoringActor
//      val request: RequestContext = requestContext
//
//      registerReceive({
//        case SystemStatus => {}
//      })
//    })
//  }
//
//  val statusStreamingRoute: Route =
//    path("status") {
//      get {
//        statusStreamingHandler
//      }
//    }
//}
2