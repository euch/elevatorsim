package org.euch.elevatorsim.domain.model.shaft

import org.euch.elevatorsim.domain.model.SafetyRecord
import org.euch.elevatorsim.domain.model.dimensions.Dimensions
import org.euch.elevatorsim.domain.model.portal.Portal
import org.euch.elevatorsim.domain.model.transport.Transport
import org.euch.elevatorsim.domain.model.winch.Winch

trait Shaft {
  val name: String
  val dimensions: Dimensions
  val portals: List[Portal]
  val transport: Transport
  val winch: Winch
  val safetyRecords: List[SafetyRecord] = List()
  val winchToCabinSpeedMultiplier: Integer = 1

}
