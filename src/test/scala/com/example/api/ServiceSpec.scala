package com.example.api

import spray.testkit.ScalatestRouteTest

class ServiceSpec extends testUtils.Spec with ScalatestRouteTest with Service {
  def actorRefFactory = system

  "Service" should {
    "return the index page" in {
      Get() ~> serviceRoute ~> check {
        responseAs[String] should include("McBurger")
      }
    }
  }
}