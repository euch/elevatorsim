package org.euch.elevatorsim.simulation.model

sealed trait Order {
  val service: Boolean = false
}
object Order {
  sealed trait Up extends Order
  sealed trait Down extends Order 
  case object NormalUp extends Up
  case object NormalDown extends Down
}
