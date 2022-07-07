package org.euch.elevatorsim

import com.typesafe.config.ConfigFactory

import java.time.Duration

object Config {
  private val rootConf = ConfigFactory.load().getConfig("app")

  object CabinConfig {
    private val elevatoConf = rootConf.getConfig("elevator.cabin")
    val widthCm: Double = elevatoConf.getInt("sizeCm.width")
    val heightCm: Double = elevatoConf.getInt("sizeCm.height")
    val depthCm: Double = elevatoConf.getInt("sizeCm.depth")
    val maxLoadKg: Double = elevatoConf.getInt("maxLoadKg")
  }
}
