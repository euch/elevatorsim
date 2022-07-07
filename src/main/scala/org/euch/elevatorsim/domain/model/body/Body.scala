package org.euch.elevatorsim.domain.model.body

import org.euch.elevatorsim.domain.model.dimensions.Dimensions

trait Body {
  val name: String
  val dimensions: Dimensions
  val weight: Double
  def space: Double = dimensions.space
}
