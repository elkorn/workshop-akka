package com.example.config

import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration.Duration

object McBurger {
  private val config: Config = ConfigFactory.load().getConfig("McBurger")
  val operationalDelay: Duration = Duration(config.getString("operational-delay"))
}
