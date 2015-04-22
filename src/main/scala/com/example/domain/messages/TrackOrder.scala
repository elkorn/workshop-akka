package com.example.domain.messages

import com.example.api.ApiMessage

case class TrackOrder(order: Order) extends ApiMessage
