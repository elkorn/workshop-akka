package com.example.api.protocols

import java.util.UUID

import com.example.domain.actors.OrderReady
import com.example.domain.messages.Order
import com.example.messages.OrderRequest
import spray.httpx.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat, deserializationError}

object JsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val orderFormat = jsonFormat7(Order.apply)
  implicit val orderRequestFormat = jsonFormat6(OrderRequest)
  implicit val orderReadyFormat = jsonFormat1(OrderReady)

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString)

    //Never execute this line
    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }
}
