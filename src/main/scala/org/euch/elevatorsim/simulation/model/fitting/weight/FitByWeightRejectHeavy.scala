package org.euch.elevatorsim.simulation.fitting.weight

import org.euch.elevatorsim.domain.model.*
import org.euch.elevatorsim.domain.model.loads.LoadGroup
import org.euch.elevatorsim.domain.model.transport.*
import org.euch.elevatorsim.simulation.model.fitting.FitResult

class FitByWeightRejectHeavy(weightLimit: Double) extends WeightFittingRules {
  override def fit(transport: Transport, newLoad: List[LoadGroup]): FitResult = {
    val newLoadSorted = newLoad.sortBy(_.weightSum);
    findSplitIdx(weightLimit, transport, newLoadSorted) match {
      case Some(idx) => FitResult.someFits(newLoadSorted.splitAt(idx))
      case None => FitResult.allFits(newLoad)
    }
  }
}



