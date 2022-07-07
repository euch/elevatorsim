package org.euch.elevatorsim.domain.model.winch

final case class SingleSpeedWinch(
    override val nominalSpeedUp: Double,
    override val nominalSpeedDown: Double,
) extends Winch {
  override def getSpeed(elapsedTime: Long, origSpeed: Double, targetSpeed: Double) = targetSpeed
}
