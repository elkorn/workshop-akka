package com.example

import akka.actor._

import scala.reflect.ClassTag

trait ActorOnDemand {
  def actorRefFactory:ActorRefFactory

  def createActor[ActorTemplate <: Actor: ClassTag](create: () => ActorTemplate): ActorRef = {
    actorRefFactory.actorOf {
      Props {
       create()
      }
    }
  }
}