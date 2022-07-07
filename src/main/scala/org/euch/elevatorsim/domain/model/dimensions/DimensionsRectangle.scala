package org.euch.elevatorsim.domain.model.dimensions

class DimensionsRectangle(val x: Double,
                          val y: Double
                         ) extends Dimensions {

  override def space: Double = x * y
}
