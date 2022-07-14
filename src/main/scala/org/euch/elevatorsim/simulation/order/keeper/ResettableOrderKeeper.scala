package org.euch.elevatorsim.simulation.order.keeper

import org.euch.elevatorsim.simulation.order.{Order, OrderDirection}

import java.time.Instant

class ResettableOrderKeeper() extends OrderKeeper {

  private val baseOrderKeeper = BaseOrderKeeper()
  private val maxRange = Range(Integer.MIN_VALUE, Integer.MAX_VALUE)

  override def setOrder(
      floorNum: Int,
      wantedDirection: OrderDirection
  ): Boolean = baseOrderKeeper.setOrder(floorNum, wantedDirection)

  override def getOrders(
      directionFilter: OrderDirection,
      floorRange: Range = maxRange
  ): List[Order] =
    baseOrderKeeper.getOrders(directionFilter, floorRange)

  override def removeOrder(
      floorNum: Int,
      directionFilter: OrderDirection
  ): Boolean =
    baseOrderKeeper.removeOrder(floorNum, directionFilter)

  def reset(): Unit = getOrders(OrderDirection.Any).foreach(o =>
    removeOrder(o.floorNum, o.wantedDirection)
  )
}
