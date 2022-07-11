package org.euch.elevatorsim.domain.model.doors

import org.euch.elevatorsim.domain.model.body.Body
import org.euch.elevatorsim.domain.model.{SafetyRecord, SafetyStatus}

trait Door extends Body {
  def safetyRecords: SafetyRecord = SafetyRecord(name, SafetyStatus.Green)
}
