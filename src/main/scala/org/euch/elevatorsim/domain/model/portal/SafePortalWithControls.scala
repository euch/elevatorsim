package org.euch.elevatorsim.domain.model.portal

import org.euch.elevatorsim.domain.model.{Controllable, SafetyRecord, SafetyStatus}
import org.euch.elevatorsim.domain.model.controls.ButtonPane
import org.euch.elevatorsim.domain.model.doors.Door

case class SafePortalWithControls(override val floorLevelZ: Double,
                                  override val name: String,
                                  shaftDoors: List[Door],
                                  override val buttonPane: ButtonPane) extends Portal with Controllable {
  override def safetyRecords: List[SafetyRecord] = shaftDoors.map(_.safetyRecords)
}
