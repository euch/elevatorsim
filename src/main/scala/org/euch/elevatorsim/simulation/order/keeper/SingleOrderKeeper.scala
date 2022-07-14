package org.euch.elevatorsim.simulation.order.keeper

import org.euch.elevatorsim.simulation.order.keeper.OrderKeeper
import org.euch.elevatorsim.simulation.order.{Order, OrderDirection}

import java.time.Instant

class SingleOrderKeeper() extends OrderKeeper {

  private var order = Option.empty[Order]
  private val maxRange = Range(Integer.MIN_VALUE, Integer.MAX_VALUE)

  override def setOrder(
                         floorNum: Int,
                         wantedDirection: OrderDirection = OrderDirection.Any
                       ): Boolean = {
    order = Some(Order(floorNum, wantedDirection, Instant.now()))
    true
  }

  override def removeOrder(
                            floorNum: Int,
                            directionFilter: OrderDirection = OrderDirection.Any
                          ): Boolean = {
    order = order
      .filterNot(o =>
        o.floorNum == floorNum && o.wantedDirection.compatible(directionFilter)
      )
    order.isEmpty
  }

  override def getOrders(
                          directionFilter: OrderDirection,
                          floorRange: Range = maxRange
                        ): List[Order] = order
    .filter(o => {
      floorRange.contains(o.floorNum) &&
        o.wantedDirection.compatible(directionFilter)
    })
    .toList

}
