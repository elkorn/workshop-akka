package com.example.domain.messages

import java.util.UUID

trait OrderMessage {
  val orderId: UUID
}