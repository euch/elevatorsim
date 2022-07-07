package org.euch.elevatorsim.domain.model.loads

import org.euch.elevatorsim.domain.model.body.Body
import org.euch.elevatorsim.domain.model.dimensions.{Dimensions, DimensionsBox}

case class Passenger(override val name: String, override val weight: Double, override val dimensions: Dimensions) extends Load {
}
