package org.euch.elevatorsim.domain.model.shaft

import org.euch.elevatorsim.domain.model.SafetyRecord
import org.euch.elevatorsim.domain.model.body.Body
import org.euch.elevatorsim.domain.model.dimensions.DimensionsBox
import org.euch.elevatorsim.domain.model.portal.Portal
import org.euch.elevatorsim.domain.model.transport.Transport
import org.euch.elevatorsim.domain.model.winch.Winch

case class BaseShaft(
    override val portals: List[Portal],
    override val name: String,
    override val dimensions: DimensionsBox,
    override val transport: Transport,
    override val winch: Winch
) extends Shaft {
  override val safetyRecords: List[SafetyRecord] =
    portals.flatMap(_.safetyRecords)
}
