package org.euch.elevatorsim.domain.model.transport

import org.euch.elevatorsim.domain.model.Controllable
import org.euch.elevatorsim.domain.model.body.Body
import org.euch.elevatorsim.domain.model.dimensions.DimensionsBox
import org.euch.elevatorsim.domain.model.loads.Load
import org.euch.elevatorsim.simulation.model.fitting.FittingRules

import javax.naming.ldap.Control

class CabinBox(
                override val name: String,
                override val weight: Double,
                override val dimensions: DimensionsBox,
                override val innerDimensions: DimensionsBox,
                override val loads: List[Load] = List.empty,
                override val defaultFloor: Option[Int]
              ) extends Transport
