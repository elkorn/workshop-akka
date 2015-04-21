package com.example.api

import spray.testkit.ScalatestRouteTest

class ServiceSpec extends testUtils.Spec with ScalatestRouteTest with Service {
  def actorRefFactory = system

  "Service" should {
    "send return a pong for GET request to ping" in {
      Get() ~> serviceRoute ~> check {
        responseAs[String] should include("Say hello")
      }
    }
  }
}