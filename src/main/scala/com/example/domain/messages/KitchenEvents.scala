package com.example.domain.messages

import java.util.UUID

trait ProductReadyEvent extends OrderMessage

case class FriesReady(orderId: UUID) extends ProductReadyEvent
case class SandwichReady(orderId: UUID) extends ProductReadyEvent
case class SaladReady(orderId: UUID) extends ProductReadyEvent
case class DrinkReady(orderId: UUID) extends ProductReadyEvent
case class CoffeeReady(orderId: UUID) extends ProductReadyEvent
case class ShakeReady(orderId: UUID) extends ProductReadyEvent