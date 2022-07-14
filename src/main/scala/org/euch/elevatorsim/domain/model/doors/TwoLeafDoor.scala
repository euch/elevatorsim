package org.euch.elevatorsim.domain.model.doors

import org.euch.elevatorsim.domain.model.dimensions.DimensionsRectangle

case class TwoLeafDoor(
    override val name: String,
    override val dimensions: DimensionsRectangle,
    override val weight: Double,
    override val openSpeedPPS: Double,
    override val closeSpeedPPS: Double
) extends PoweredDoor
