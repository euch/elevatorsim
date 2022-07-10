package org.euch.elevatorsim.domain.model.winch

import org.euch.elevatorsim.domain.model.Direction
import org.euch.elevatorsim.domain.model.body.Body

trait Winch extends Body {
  val nominalSpeedUp: Double
  val nominalSpeedDown: Double
  def getNominalSpeed(direction: Direction): Double = direction match {
    case Direction.Up   => nominalSpeedUp
    case Direction.Down => nominalSpeedDown
  }
}
