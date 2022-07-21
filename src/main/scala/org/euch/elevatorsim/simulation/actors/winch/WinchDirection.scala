package org.euch.elevatorsim.simulation.actors.winch

sealed trait WinchDirection

object WinchDirection {
  case object Up extends WinchDirection

  case object Down extends WinchDirection
}
