package org.euch.elevatorsim.domain.model

sealed trait Direction
object Direction {
  case object Up extends Direction
  case object Down extends Direction
}
