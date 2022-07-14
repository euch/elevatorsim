package org.euch.elevatorsim.simulation.orders

import org.euch.elevatorsim.simulation.orders.OrderDirection

import java.time.Instant
class BaseOrderKeeper() extends OrderKeeper {

  private var orders = Set.empty[Order]
  private val maxRange = Range(Integer.MIN_VALUE, Integer.MAX_VALUE)

  override def setOrder(
      floorNum: Int,
      wantedDirection: OrderDirection = OrderDirection.Any
  ): Boolean = {
    val order = Order(floorNum, wantedDirection, Instant.now())
    orders = orders ++ Set(order)
    orders.contains(order)
  }

  override def removeOrder(
      floorNum: Int,
      directionFilter: OrderDirection = OrderDirection.Any
  ): Boolean = {
    val sizeBefore = orders.size
    orders = orders
      .filterNot(o =>
        o.floorNum == floorNum && o.wantedDirection.compatible(directionFilter)
      )
    orders.size < sizeBefore
  }

  override def getOrders(
      directionFilter: OrderDirection,
      floorRange: Range = maxRange
  ): List[Order] = orders
    .filter(o => {
      floorRange.contains(o.floorNum) &&
      o.wantedDirection.compatible(directionFilter)
    })
    .toList
    .sortBy(_.floorNum)

}
