package org.euch.elevatorsim.domain.model

import org.euch.elevatorsim.domain.model.controls.ButtonPane

sealed trait SafetyStatus
object SafetyStatus {
  case object Green extends SafetyStatus
  case object Red extends SafetyStatus
}
case class SafetyRecord(sensorName: String, safetyStatus: SafetyStatus)
