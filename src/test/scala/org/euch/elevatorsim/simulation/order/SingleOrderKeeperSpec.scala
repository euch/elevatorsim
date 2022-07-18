package org.euch.elevatorsim.simulation.order

import org.euch.elevatorsim.simulation.order.keeper.{OrderDirection, SingleOrderKeeper}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SingleOrderKeeperSpec extends AnyFlatSpec with Matchers {
  private val testObj = new SingleOrderKeeper()
  private val allDirections =
    List(OrderDirection.Any, OrderDirection.Up, OrderDirection.Down)
  private val fullRange = Range(Integer.MIN_VALUE, Integer.MAX_VALUE)

  // initially empty
  allDirections.foreach(d => {
    testObj.getOrders(d) shouldBe List.empty
    testObj.getOrders(d, fullRange) shouldBe List.empty
  })

  // add first order and we can find it
  testObj.setOrder(1, OrderDirection.Up) shouldBe true
  testObj
    .getOrders(OrderDirection.Up)
    .map(o => (o.floorNum, o.wantedDirection)) shouldBe
    List((1, OrderDirection.Up))
  testObj
    .getOrders(OrderDirection.Down)
    .map(o => (o.floorNum, o.wantedDirection)) shouldBe
    List.empty
  testObj
    .getOrders(OrderDirection.Any)
    .map(o => (o.floorNum, o.wantedDirection)) shouldBe
    List((1, OrderDirection.Up))

  // add new order and it will replace first one
  testObj.setOrder(2, OrderDirection.Down) shouldBe true
  testObj
    .getOrders(OrderDirection.Up)
    .map(o => (o.floorNum, o.wantedDirection)) shouldBe
    List.empty
  testObj
    .getOrders(OrderDirection.Down)
    .map(o => (o.floorNum, o.wantedDirection)) shouldBe
    List((2, OrderDirection.Down))
  testObj
    .getOrders(OrderDirection.Any)
    .map(o => (o.floorNum, o.wantedDirection)) shouldBe
    List((2, OrderDirection.Down))

  // wouldn't delete - direction is different
  testObj.removeOrder(2, OrderDirection.Up)
  testObj
    .getOrders(OrderDirection.Down)
    .map(o => (o.floorNum, o.wantedDirection)) shouldBe
    List((2, OrderDirection.Down))

  // delete by 'any' direction
  testObj.removeOrder(2, OrderDirection.Any)
  testObj
    .getOrders(OrderDirection.Any)
    .map(o => (o.floorNum, o.wantedDirection)) shouldBe
    List.empty
}
