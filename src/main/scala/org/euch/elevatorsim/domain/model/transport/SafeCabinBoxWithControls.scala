package org.euch.elevatorsim.domain.model.transport

import org.euch.elevatorsim.domain.model.Controllable
import org.euch.elevatorsim.domain.model.body.Body
import org.euch.elevatorsim.domain.model.controls.ButtonPane
import org.euch.elevatorsim.domain.model.dimensions.DimensionsBox
import org.euch.elevatorsim.domain.model.doors.Door
import org.euch.elevatorsim.domain.model.loads.Load
import org.euch.elevatorsim.simulation.model.fitting.FittingRules

class SafeCabinBoxWithControls(
    override val name: String,
    override val weight: Double,
    override val dimensions: DimensionsBox,
    override val innerDimensions: DimensionsBox,
    override val loads: List[Load],
    override val buttonPane: ButtonPane,
    override val doors: List[Door],
    override val defaultFloor: Option[String]
) extends SafeTransport
    with Controllable
