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
    }
  })
}

trait StatusStreamingActor extends StreamingActor {
  val statusMonitorActor: ActorRef
  val request: RequestContext

  val responseStart = HttpResponse(entity = HttpEntity(`application/json`, "{\"status\": {"))

  def finish() {
    request.responder ! MessageChunk("}}")
  }

  request.responder ! ChunkedResponseStart(responseStart)
  statusMonitorActor ! com.example.domain.messages.GetSystemStatus
}