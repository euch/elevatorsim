package org.euch.elevatorsim.domain.model.transport

import org.euch.elevatorsim.domain.model.body.Body
import org.euch.elevatorsim.domain.model.dimensions.Dimensions
import org.euch.elevatorsim.domain.model.doors.Door
import org.euch.elevatorsim.domain.model.loads.Load

trait SafeTransport extends Transport {
  val doors: List[Door]

  override def totalWeight: Double = super.totalWeight + doors.map(_.weight).sum
}
