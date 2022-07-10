package org.euch.elevatorsim.domain.model.winch

import org.euch.elevatorsim.domain.model.dimensions.Dimensions

final case class SingleSpeedWinch(
    override val nominalSpeedUp: Double,
    override val nominalSpeedDown: Double,
    override val weight: Double,
    override val name: String,
    override val dimensions: Dimensions
) extends Winch
