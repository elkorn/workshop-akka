package com.example.domain.messages

import java.util.UUID

case class Order(orderId: UUID,
                 sandwiches: Int,
                 fries: Int,
                 salads: Int,
                 drinks: Int,
                 coffees: Int,
                 shakes: Int) 
  extends OrderMessage {
    def add(sandwiches: Int = 0,
            fries: Int = 0,
            salads: Int = 0,
            drinks: Int = 0,
            coffees: Int = 0,
            shakes: Int = 0) =  
         Order(
           this.orderId, 
           this.sandwiches + sandwiches, 
           this.fries + fries, 
           this.salads + salads, 
           this.drinks + drinks, 
           this.coffees + coffees, 
           this.shakes + shakes)

    def hasAllProducts(required: Order) = 
      sandwiches == required.sandwiches &&
      fries == required.fries &&
      salads == required.salads &&
      drinks == required.drinks &&
      coffees == required.coffees &&
      shakes == required.shakes

    override def toString() = 
      s"[$orderId] sandwiches: $sandwiches, fries: $fries, salads: $salads, drinks: $drinks, coffees: $coffees, shakes: $shakes"
  }
