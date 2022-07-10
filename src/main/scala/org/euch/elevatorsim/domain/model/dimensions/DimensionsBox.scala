package org.euch.elevatorsim.domain.model.dimensions

class DimensionsBox(val x: Double, val y: Double, val z: Double)
    extends Dimensions {

  def volume: Double = x * y * z

  override def space: Double = x * y
}
