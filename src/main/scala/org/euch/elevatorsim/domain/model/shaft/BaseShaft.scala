package org.euch.elevatorsim.domain.model.shaft

import org.euch.elevatorsim.domain.model.Floor
import org.euch.elevatorsim.domain.model.body.Body
import org.euch.elevatorsim.domain.model.dimensions.DimensionsBox
import org.euch.elevatorsim.domain.model.transport.Transport
import org.euch.elevatorsim.domain.model.winch.Winch

case class BaseShaft(
    override val floors: List[Floor],
    override val name: String,
    override val dimensions: DimensionsBox,
    override val transport: Transport,
    override val winch: Winch
) extends Shaft
