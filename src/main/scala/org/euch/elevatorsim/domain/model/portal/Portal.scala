package org.euch.elevatorsim.domain.model.portal

import org.euch.elevatorsim.domain.model.{SafetyRecord, SafetyStatus}

trait Portal {
  val name: String
  val floorLevelZ: Double
  def safetyRecords: List[SafetyRecord] = List()
}
