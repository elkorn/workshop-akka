package com.example.messages

import com.example.domain.messages.Order
import spray.routing.RequestContext

case class TrackOrder(order: Order, request: RequestContext)
