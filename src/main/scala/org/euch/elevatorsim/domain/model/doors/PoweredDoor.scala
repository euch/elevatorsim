package org.euch.elevatorsim.domain.model.doors

trait PoweredDoor extends Door {
  // percent per second
  val openSpeedPPS: Double
  // percent per second
  val closeSpeedPPS: Double
}
