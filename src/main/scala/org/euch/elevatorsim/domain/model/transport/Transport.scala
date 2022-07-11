package org.euch.elevatorsim.domain.model.transport

import org.euch.elevatorsim.domain.model.body.Body
import org.euch.elevatorsim.domain.model.dimensions.Dimensions
import org.euch.elevatorsim.domain.model.loads.Load

trait Transport extends Body {
  val innerDimensions: Dimensions
  val loads: List[Load]
  val defaultFloor: Option[String]

  def totalWeight: Double = weight + loads.map(_.weight).sum
}
