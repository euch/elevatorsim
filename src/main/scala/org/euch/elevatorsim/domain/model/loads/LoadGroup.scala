package org.euch.elevatorsim.domain.model.loads

import org.euch.elevatorsim.domain.model.body.Body

case class LoadGroup(
                      name: String,
                      destinationFloor: Int,
                      elements: List[Load],
                      loadSpeedPPS: Double,
                      unloadSpeedPPS: Double
                    ) {
  def spaceSum: Double = elements.map(_.space).sum

  def weightSum: Double = elements.map(_.weight).sum
}
