package org.euch.elevatorsim.domain.model.controls

case class StickyButton(override val name: String,
                        stick: Boolean = false,
                        override val pressed: Boolean = false) extends Button
