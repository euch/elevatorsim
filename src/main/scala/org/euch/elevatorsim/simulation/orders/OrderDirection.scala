package org.euch.elevatorsim.simulation.orders

trait OrderDirection {
  def compatible(orderDirection: OrderDirection): Boolean = {
    if (Set(orderDirection, this).contains(OrderDirection.Any))
      true
    else {
      orderDirection == this
    }
  }
}
object OrderDirection extends OrderDirection {
  case object Any extends OrderDirection
  case object Down extends OrderDirection
  case object Up extends OrderDirection
}
