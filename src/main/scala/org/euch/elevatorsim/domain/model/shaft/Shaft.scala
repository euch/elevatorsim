package org.euch.elevatorsim.domain.model.shaft

import org.euch.elevatorsim.domain.model.Floor
import org.euch.elevatorsim.domain.model.dimensions.Dimensions
import org.euch.elevatorsim.domain.model.transport.Transport
import org.euch.elevatorsim.domain.model.winch.Winch

trait Shaft {
  val name: String
  val dimensions: Dimensions
  val floors: List[Floor]
  val transport: Transport
  val winch: Winch
  val winchToCabinSpeedMultiplier: Integer = 1

}
