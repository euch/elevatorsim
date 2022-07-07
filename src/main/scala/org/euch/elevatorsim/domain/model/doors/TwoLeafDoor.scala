package org.euch.elevatorsim.domain.model.doors

case class TwoLeafDoor(override val name: String,
                       override val open: Boolean = false) extends SafeDoor
