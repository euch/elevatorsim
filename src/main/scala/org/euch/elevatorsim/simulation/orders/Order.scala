package org.euch.elevatorsim.simulation.orders

import java.time.Instant

case class Order(floorNum: Int, wantedDirection: OrderDirection, added: Instant)
