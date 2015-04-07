package com.example.api

/**
 * Created by elkorn on 4/15/15.
 */

import org.scalatest._
import spray.testkit.{ScalatestRouteTest}
import org.scalatest.{Matchers}


class ServiceSpec extends FlatSpec with ScalatestRouteTest with Service with Matchers with BeforeAndAfterAll {
  def actorRefFactory = system

  "Service" should "send return a pong for GET request to ping" in {
    Get() ~> myRoute ~> check {
      responseAs[String] should include("Say hello")
    }
  }
}
