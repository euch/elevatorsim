package org.euch.elevatorsim.simulation.order.keeper

import org.euch.elevatorsim.simulation.order.keeper.OrderDirection

import java.time.Instant

case class Order(floorNum: Int, wantedDirection: OrderDirection, added: Instant)
