package org.euch.elevatorsim.simulation.model.fitting

import org.euch.elevatorsim.domain.model.loads.LoadGroup

case class FitResult(fits: List[LoadGroup], rejected: List[LoadGroup])

object FitResult {
  def allFits(newLoad: List[LoadGroup]): FitResult = FitResult(newLoad, List())

  def someFits(
      fitAndRejectedLoad: (List[LoadGroup], List[LoadGroup])
  ): FitResult = FitResult(fitAndRejectedLoad._1, fitAndRejectedLoad._2)
}
