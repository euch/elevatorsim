package org.euch.elevatorsim.simulation.model.fitting.space

import org.euch.elevatorsim.domain.model.*
import org.euch.elevatorsim.domain.model.loads.LoadGroup
import org.euch.elevatorsim.domain.model.transport.*
import org.euch.elevatorsim.simulation.model.fitting.space.SpaceFittingRules
import org.euch.elevatorsim.simulation.model.fitting.FitResult

class FitBySquareRejectByOrder extends SpaceFittingRules {

  override def fit(
      transport: Transport,
      newLoad: List[LoadGroup]
  ): FitResult = {
    findSquareSplitIdx(transport, newLoad) match {
      case Some(idx) => FitResult.someFits(newLoad.splitAt(idx))
      case None      => FitResult.allFits(newLoad)
    }
  }

}
