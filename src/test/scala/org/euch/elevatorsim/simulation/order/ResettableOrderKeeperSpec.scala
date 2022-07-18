package org.euch.elevatorsim.simulation.order

import org.euch.elevatorsim.simulation.order.keeper.{OrderDirection, ResettableOrderKeeper}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ResettableOrderKeeperSpec extends AnyFlatSpec with Matchers {
  private val testObj = new ResettableOrderKeeper()
  private val allDirections =
    List(OrderDirection.Any, OrderDirection.Up, OrderDirection.Down)
  private val fullRange = Range(Integer.MIN_VALUE, Integer.MAX_VALUE)

  // initially empty
  allDirections.foreach(d => {
    testObj.getOrders(d) shouldBe List.empty
    testObj.getOrders(d, fullRange) shouldBe List.empty
  })

  // add in wrong order with expected duplicates
  testObj.setOrder(1, OrderDirection.Up) shouldBe true
  testObj.setOrder(1, OrderDirection.Up) shouldBe true
  testObj.setOrder(2, OrderDirection.Any) shouldBe true
  testObj.setOrder(3, OrderDirection.Up) shouldBe true
  testObj.setOrder(3, OrderDirection.Down) shouldBe true
  testObj.setOrder(5, OrderDirection.Any) shouldBe true
  testObj.setOrder(4, OrderDirection.Down) shouldBe true

  // count should match
  testObj.getOrders(OrderDirection.Any).size shouldBe 7
  testObj.getOrders(OrderDirection.Any, fullRange).size shouldBe 7
  testObj.getOrders(OrderDirection.Up).size shouldBe 5
  testObj.getOrders(OrderDirection.Up, fullRange).size shouldBe 5
  testObj.getOrders(OrderDirection.Down).size shouldBe 4
  testObj.getOrders(OrderDirection.Down, fullRange).size shouldBe 4

  testObj.getOrders(OrderDirection.Any, Range(2, 5)).size shouldBe 4
  testObj.getOrders(OrderDirection.Up, Range(2, 5)).size shouldBe 2
  testObj.getOrders(OrderDirection.Down, Range(2, 5)).size shouldBe 3

  // remove both 1st floor orders
  testObj.removeOrder(1)

  // count should match
  testObj.getOrders(OrderDirection.Any).size shouldBe 5
  testObj.getOrders(OrderDirection.Any, fullRange).size shouldBe 5
  testObj.getOrders(OrderDirection.Up).size shouldBe 3
  testObj.getOrders(OrderDirection.Up, fullRange).size shouldBe 3
  testObj.getOrders(OrderDirection.Down).size shouldBe 4
  testObj.getOrders(OrderDirection.Down, fullRange).size shouldBe 4

  // remove one specific 3rd floor order
  testObj.removeOrder(3, OrderDirection.Up)

  // count should match
  testObj.getOrders(OrderDirection.Any).size shouldBe 4
  testObj.getOrders(OrderDirection.Any, fullRange).size shouldBe 4
  testObj.getOrders(OrderDirection.Up).size shouldBe 2
  testObj.getOrders(OrderDirection.Up, fullRange).size shouldBe 2
  testObj.getOrders(OrderDirection.Down).size shouldBe 4
  testObj.getOrders(OrderDirection.Down, fullRange).size shouldBe 4

  // make sure result is ordered and directions are the same
  testObj
    .getOrders(OrderDirection.Any)
    .map(o => (o.floorNum, o.wantedDirection)) shouldBe
    List(
      (2, OrderDirection.Any),
      (3, OrderDirection.Down),
      (4, OrderDirection.Down),
      (5, OrderDirection.Any)
    )

  // reset (clear all)
  testObj.reset()

  // should be empty
  allDirections.foreach(d => {
    testObj.getOrders(d) shouldBe List.empty
    testObj.getOrders(d, fullRange) shouldBe List.empty
  })

}
