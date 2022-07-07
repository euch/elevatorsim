package org.euch.elevatorsim.simulation.fsm

import org.euch.elevatorsim.domain.model.winch.Winch
import org.euch.elevatorsim.simulation.model.Order
import java.time.Instant

class WinchFSM(winch: Winch) {

  var prevSpeed = 0d
  var targetSpeed = 0d
  var updated: Instant = Instant.now()

  def getSpeed(now: Instant): Double = {
    val elapsedTime = now.getEpochSecond() - updated.getEpochSecond()
    winch.getSpeed(elapsedTime, prevSpeed, targetSpeed)
  }

  def start(order: Order, now: Instant) = {
    if (getSpeed(now) == 0) {
      targetSpeed = order match {
        case Order.NormalUp   => winch.nominalSpeedUp
        case Order.NormalDown => winch.nominalSpeedDown
      }
      updated = now
    }
  }

  def stop(now: Instant) = {
    if (getSpeed(now) != 0) {
      prevSpeed = targetSpeed
      targetSpeed = 0
      updated = now
    }
  }
}
