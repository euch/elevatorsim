package org.euch.elevatorsim.domain.model.controls

case class StickyButton(
    stick: Boolean = false,
    override val pressed: Boolean = false
) extends Button
