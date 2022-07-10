package org.euch.elevatorsim.domain.model.winch

import org.euch.elevatorsim.domain.model.dimensions.Dimensions

case class TwoSpeedWinch(
    override val nominalSpeedUp: Double,
    override val nominalSpeedDown: Double,
    override val weight: Double,
    override val name: String,
    override val dimensions: Dimensions,
    speedUpAccelerations: VerticalAccelerations,
    slowDownAccelerations: VerticalAccelerations
) extends Winch

case class VerticalAccelerations(upwards: Double, downwards: Double)
