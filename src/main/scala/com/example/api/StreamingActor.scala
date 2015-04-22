package com.example.api

import akka.actor.{ActorRef, ActorLogging}
import com.example.ComposableActor
import spray.can.Http
import spray.http._
import MediaTypes._
import spray.routing.RequestContext

trait StreamingActor extends ComposableActor with ActorLogging {
  registerReceive({
    case x: Http.ConnectionClosed => {
      log.warning("Stopping response streaming due to {}", x)
    }
  })
}

trait StatusStreamingActor extends StreamingActor {
  val statusMonitorActor: ActorRef
  val request: RequestContext

  val responseStart = HttpResponse(entity = HttpEntity(`application/json`, "{\"status\": {"))

  def finishWith(chunk: MessageChunk) {
    request.responder ! MessageChunk("}}")
    request.responder ! ChunkedMessageEnd
  }

  registerReceive({
    case _ => {}
//    case ReportEvent(report) => {
//      finishWith()
//      request.responder ! MessageChunk(report map { case (k, v) => s""""$k":"${v.id}"""" } mkString("", ",", "}}"))
//      request.responder ! ChunkedMessageEnd
//      context.stop(self)
//    }
  })

  request.responder ! ChunkedResponseStart(responseStart)
  statusMonitorActor ! com.example.domain.messages.GetSystemStatus
}