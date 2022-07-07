package org.euch.elevatorsim.domain.model.winch

trait Winch {
  val nominalSpeedUp: Double
  val nominalSpeedDown: Double
  def getSpeed(elapsedTime: Long, origSpeed: Double, targetSpeed: Double): Double
}
