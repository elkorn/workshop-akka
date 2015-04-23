package com.example.config

import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration.{Duration, FiniteDuration}

object McBurger {
  lazy val operationalDelay: FiniteDuration =
    FiniteDuration(
      Duration(
        config.getString("operational-delay")).toMillis,
      concurrent.duration.MILLISECONDS)

  private val config: Config = ConfigFactory.load().getConfig("McBurger")
}
