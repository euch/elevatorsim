package org.euch.elevatorsim.simulation.order.keeper

import org.euch.elevatorsim.simulation.order.{Order, OrderDirection}

trait OrderKeeper {
  def setOrder(
                floorNum: Int,
                wantedDirection: OrderDirection = OrderDirection.Any
              ): Boolean

  def removeOrder(
                   floorNum: Int,
                   directionFilter: OrderDirection = OrderDirection.Any
                 ): Boolean

  def getOrders(
                 directionFilter: OrderDirection,
                 floorRange: Range = Range(Integer.MIN_VALUE, Integer.MAX_VALUE)
               ): List[Order]

}
