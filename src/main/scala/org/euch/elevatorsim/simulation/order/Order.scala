package org.euch.elevatorsim.simulation.order

import java.time.Instant

case class Order(floorNum: Int, wantedDirection: OrderDirection, added: Instant)
