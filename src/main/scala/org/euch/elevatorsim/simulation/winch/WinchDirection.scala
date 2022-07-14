package org.euch.elevatorsim.simulation.winch

sealed trait WinchDirection

object WinchDirection {
  case object Up extends WinchDirection

  case object Down extends WinchDirection
}
