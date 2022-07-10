package org.euch.elevatorsim.simulation.model.fitting.weight

import org.euch.elevatorsim.domain.model.loads.LoadGroup
import org.euch.elevatorsim.domain.model.transport.Transport
import org.euch.elevatorsim.simulation.model.fitting.{FitResult, FittingRules}

trait WeightFittingRules extends FittingRules {
  def fit(transport: Transport, newLoad: List[LoadGroup]): FitResult

  protected def findSplitIdx(
      weightLimit: Double,
      transport: Transport,
      newLoadSorted: List[LoadGroup]
  ): Option[Int] = {
    val usedWeight = transport.totalWeight
    var requiredWeight = usedWeight
    newLoadSorted.map(_.weightSum).zipWithIndex.foreach { case (weight, i) =>
      requiredWeight = requiredWeight + weight
      if (requiredWeight > weightLimit) {
        return Some(i)
      }
    }
    None
  }
}
