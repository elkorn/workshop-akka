package com.example.domain.messages

import java.util.UUID

case class Order(orderId: UUID,
                 sandwiches: Int,
                 fries: Int,
                 salads: Int,
                 drinks: Int,
                 coffees: Int,
                 shakes: Int)
  extends OrderMessage
