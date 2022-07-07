package org.euch.elevatorsim.simulation.model.fitting

import org.euch.elevatorsim.domain.model.loads.LoadGroup
import org.euch.elevatorsim.domain.model.transport.Transport

trait FittingRules {
  def fit(transport: Transport, newLoad: List[LoadGroup]): FitResult
}
