package org.euch.elevatorsim.domain.model.controls

case class SimpleButtonPane(override val name: String,
                            override val buttons: Set[Button]) extends ButtonPane {
}
