package org.euch.elevatorsim.domain.model.doors

import org.euch.elevatorsim.domain.model.{SafetyRecord, SafetyStatus}

trait Door {
  val name: String
  val open: Boolean
  def safetyRecords: SafetyRecord = SafetyRecord(name, SafetyStatus.Green)
}
