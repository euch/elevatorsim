package org.euch.elevatorsim.domain.model.winch

import org.euch.elevatorsim.domain.model.body.Body
import org.euch.elevatorsim.simulation.winch.WinchDirection

trait Winch extends Body {
  val nominalSpeedUp: Double
  val nominalSpeedDown: Double

  def getNominalSpeed(direction: WinchDirection): Double = direction match {
    case WinchDirection.Up => nominalSpeedUp
    case WinchDirection.Down => nominalSpeedDown
  }
}
