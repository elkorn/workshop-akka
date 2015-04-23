package com.example.config

import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration.{Duration, FiniteDuration}

object McBurger {
  lazy val operationalDelay: FiniteDuration = finiteDurationFromConfig("operational-delay.value")
  lazy val operationalDelayVariance: FiniteDuration = finiteDurationFromConfig("operational-delay.variance")
  private val config: Config = ConfigFactory.load().getConfig("McBurger")

  private def finiteDurationFromConfig(key: String): FiniteDuration =
    FiniteDuration(
      Duration(
        config.getString(key)).toMillis,
      concurrent.duration.MILLISECONDS)
}
