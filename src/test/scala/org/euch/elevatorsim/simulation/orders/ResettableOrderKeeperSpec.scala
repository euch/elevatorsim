package org.euch.elevatorsim.simulation.orders

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ResettableOrderKeeperSpec extends AnyFlatSpec with Matchers {
  private val orders = new ResettableOrderKeeper()
  private val allDirections =
    List(OrderDirection.Any, OrderDirection.Up, OrderDirection.Down)
  private val fullRange = Range(Integer.MIN_VALUE, Integer.MAX_VALUE)

  // initially empty
  allDirections.foreach(d => {
    orders.getOrders(d) shouldBe List.empty
    orders.getOrders(d, fullRange) shouldBe List.empty
  })

  // add in wrong order with expected duplicates
  orders.setOrder(1, OrderDirection.Up) shouldBe true
  orders.setOrder(1, OrderDirection.Up) shouldBe true
  orders.setOrder(2, OrderDirection.Any) shouldBe true
  orders.setOrder(3, OrderDirection.Up) shouldBe true
  orders.setOrder(3, OrderDirection.Down) shouldBe true
  orders.setOrder(5, OrderDirection.Any) shouldBe true
  orders.setOrder(4, OrderDirection.Down) shouldBe true

  // count should match
  orders.getOrders(OrderDirection.Any).size shouldBe 7
  orders.getOrders(OrderDirection.Any, fullRange).size shouldBe 7
  orders.getOrders(OrderDirection.Up).size shouldBe 5
  orders.getOrders(OrderDirection.Up, fullRange).size shouldBe 5
  orders.getOrders(OrderDirection.Down).size shouldBe 4
  orders.getOrders(OrderDirection.Down, fullRange).size shouldBe 4

  orders.getOrders(OrderDirection.Any, Range(2, 5)).size shouldBe 4
  orders.getOrders(OrderDirection.Up, Range(2, 5)).size shouldBe 2
  orders.getOrders(OrderDirection.Down, Range(2, 5)).size shouldBe 3

  // remove both 1st floor orders
  orders.removeOrder(1)

  // count should match
  orders.getOrders(OrderDirection.Any).size shouldBe 5
  orders.getOrders(OrderDirection.Any, fullRange).size shouldBe 5
  orders.getOrders(OrderDirection.Up).size shouldBe 3
  orders.getOrders(OrderDirection.Up, fullRange).size shouldBe 3
  orders.getOrders(OrderDirection.Down).size shouldBe 4
  orders.getOrders(OrderDirection.Down, fullRange).size shouldBe 4

  // remove one specific 3rd floor order
  orders.removeOrder(3, OrderDirection.Up)

  // count should match
  orders.getOrders(OrderDirection.Any).size shouldBe 4
  orders.getOrders(OrderDirection.Any, fullRange).size shouldBe 4
  orders.getOrders(OrderDirection.Up).size shouldBe 2
  orders.getOrders(OrderDirection.Up, fullRange).size shouldBe 2
  orders.getOrders(OrderDirection.Down).size shouldBe 4
  orders.getOrders(OrderDirection.Down, fullRange).size shouldBe 4

  // make sure result is ordered and directions are the same
  orders
    .getOrders(OrderDirection.Any)
    .map(o => (o.floorNum, o.wantedDirection)) shouldBe
    List(
      (2, OrderDirection.Any),
      (3, OrderDirection.Down),
      (4, OrderDirection.Down),
      (5, OrderDirection.Any)
    )

  // reset (clear all)
  orders.reset()

  // should be empty
  allDirections.foreach(d => {
    orders.getOrders(d) shouldBe List.empty
    orders.getOrders(d, fullRange) shouldBe List.empty
  })

}
