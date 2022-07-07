package org.euch.elevatorsim.domain.model.transport

import org.euch.elevatorsim.domain.model.body.Body
import org.euch.elevatorsim.domain.model.dimensions.{DimensionsBox, DimensionsRectangle}
import org.euch.elevatorsim.domain.model.loads.Load
import org.euch.elevatorsim.simulation.model.fitting.FittingRules

import javax.print.attribute.standard.Destination

class Platform(override val name: String,
               override val dimensions: DimensionsBox,
               override val innerDimensions: DimensionsRectangle,
               override val weight: Double,
               override val loads: List[Load],
               override val defaultFloor: Option[String]) extends Transport {
}
