package org.euch.elevatorsim.domain.model

import org.euch.elevatorsim.domain.model.controls.ButtonPane

trait Controllable {
  val buttonPane: ButtonPane
}
