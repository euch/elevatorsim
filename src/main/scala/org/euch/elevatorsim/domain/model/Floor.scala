package org.euch.elevatorsim.domain.model

import org.euch.elevatorsim.domain.model.portal.Portal

case class Floor(number: Int, portals: List[Portal])
