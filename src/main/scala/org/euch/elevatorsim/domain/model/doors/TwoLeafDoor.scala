package org.euch.elevatorsim.domain.model.doors

import org.euch.elevatorsim.domain.model.dimensions.DimensionsRectangle

case class TwoLeafDoor(
                        override val name: String,
                        override val open: Boolean = false,
                        override val dimensions: DimensionsRectangle,
                        override val weight: Double,
                        override val openSpeed: Double,
                        override val closeSpeed: Double
) extends SafeDoor with PoweredDoor
