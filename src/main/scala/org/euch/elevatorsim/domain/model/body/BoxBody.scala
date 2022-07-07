package org.euch.elevatorsim.domain.model.body

import org.euch.elevatorsim.domain.model.dimensions.DimensionsBox

case class BoxBody(override val dimensions: DimensionsBox, override val name: String, override val weight: Double) extends Body {
  override def space: Double = dimensions.space

}
