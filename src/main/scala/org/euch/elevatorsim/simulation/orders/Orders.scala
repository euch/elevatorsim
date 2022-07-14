package org.euch.elevatorsim.simulation.orders

import org.euch.elevatorsim.simulation.orders.OrderDirection

import java.time.Instant

class Orders() {

  private var orders = Set.empty[Order]

  def setOrder(
      floorNum: Int,
      wantedDirection: OrderDirection = OrderDirection.Any
  ): Boolean = {
    val order = Order(floorNum, wantedDirection, Instant.now())
    orders = orders ++ Set(order)
    orders.contains(order)
  }

  def removeOrder(
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

  def getOrders(
      directionFilter: OrderDirection,
      floorRange: Range = Range(Integer.MIN_VALUE, Integer.MAX_VALUE)
  ): List[Order] = orders
    .filter(o => {
      floorRange.contains(o.floorNum) &&
      o.wantedDirection.compatible(directionFilter)
    })
    .toList
    .sortBy(_.floorNum)

}
