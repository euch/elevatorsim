package org.euch.elevatorsim.domain.model.doors

import org.euch.elevatorsim.domain.model.{SafetyRecord, SafetyStatus}

trait PoweredDoor {
  val openSpeed: Double
  val closeSpeed: Double
}
