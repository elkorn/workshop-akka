package com.example.domain.messages

import com.example.api.ApiMessage
import spray.routing.RequestContext

case class TrackOrder(order: Order, request: RequestContext) extends ApiMessage
