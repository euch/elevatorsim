package org.euch.elevatorsim.simulation.model.fitting.space

import org.euch.elevatorsim.domain.model.loads.LoadGroup
import org.euch.elevatorsim.domain.model.transport.Transport
import org.euch.elevatorsim.simulation.model.fitting.{FitResult, FittingRules}

trait SpaceFittingRules extends FittingRules {
  def fit(transport: Transport, newLoad: List[LoadGroup]): FitResult

  /** Fitting by Square (x * y) Finds element i in newLoadSorted which does not
    * fit transport
    *
    * @param transport
    *   Cabin, Platform, etc
    * @param newLoadSorted
    *   Group of new 'loads' such as boxes or passengers
    * @return
    *   index of element which does not fit, or None if transport has enough
    *   space for all
    */
  protected def findSquareSplitIdx(
      transport: Transport,
      newLoadSorted: List[LoadGroup]
  ): Option[Int] = {
    val usedSpace = transport.loads.map(_.dimensions.space).sum
    var requiredSpace = usedSpace
    newLoadSorted.map(_.spaceSum).zipWithIndex.foreach { case (space, i) =>
      requiredSpace = requiredSpace + space
      if (requiredSpace > transport.innerDimensions.space) {
        return Some(i)
      }
    }
    None
  }
}
