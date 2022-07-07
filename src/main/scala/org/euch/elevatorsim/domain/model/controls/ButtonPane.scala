package org.euch.elevatorsim.domain.model.controls

trait ButtonPane {
  val name: String
  val buttons: Set[Button]
}
