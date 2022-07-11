package org.euch.elevatorsim.domain.model.doors

import org.euch.elevatorsim.domain.model.{SafetyRecord, SafetyStatus}

trait SafeDoor extends Door {
  val open: Boolean
  override def safetyRecords: SafetyRecord =
    SafetyRecord(name, if (open) SafetyStatus.Red else SafetyStatus.Green)
}
