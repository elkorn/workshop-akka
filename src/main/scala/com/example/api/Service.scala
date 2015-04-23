package com.example.api

import spray.routing._

// this trait defines our service behavior independently from the service actor
trait Service extends HttpService {
  val serviceRoute =
    path("") {
      get {
        getFromResource("web/index.html")
      }
    }
}

