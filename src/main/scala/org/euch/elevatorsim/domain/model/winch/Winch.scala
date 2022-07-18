package org.euch.elevatorsim.domain.model.winch

import org.euch.elevatorsim.domain.model.body.Body
import org.euch.elevatorsim.simulation.winch.WinchDirection

trait Winch extends Body {
  val nominalSpeedUp: Double
  val nominalSpeedDown: Double


}
