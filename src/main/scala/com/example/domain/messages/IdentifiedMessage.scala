package com.example.domain.messages

import java.util.UUID

trait IdentifiedMessage {
  val id: UUID
}