package com.example.api

import spray.http.MediaTypes._
import spray.routing._

// this trait defines our service behavior independently from the service actor
trait Service extends HttpService with Protocols {
  val serviceRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) {
          // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>Say hello to
                  <i>spray-routing</i>
                  on
                  <i>spray-can</i>
                  !</h1>
              </body>
            </html>
          }
        }
      }
  }
}

