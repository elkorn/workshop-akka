package com.example.api.protocols

import java.util.UUID

import com.example.domain.actors.OrderReady
import com.example.domain.messages.Order
import spray.httpx.SprayJsonSupport
import spray.json.{RootJsonFormat, JsValue, JsString, DefaultJsonProtocol, deserializationError}

object JsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString)

    //Never execute this line
    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val orderFormat = jsonFormat7(Order)
  implicit val orderReadyFormat = jsonFormat1(OrderReady)
}
