package org.euch.elevatorsim.simulation.order.router

import org.euch.elevatorsim.simulation.order.keeper.OrderKeeper

/** @constructor
  *   orderKeepers sorted by priority, most important first!
  */
trait OrderRouter(orderKeepers: List[OrderKeeper]) {

  def next(currentFloorNum: Int, routeDirection: RouteDirection): Option[Int]

}
