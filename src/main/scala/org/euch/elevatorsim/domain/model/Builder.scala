package org.euch.elevatorsim.domain.model

import org.euch.elevatorsim.domain.model.dimensions.{Dimensions, DimensionsBox}
import org.euch.elevatorsim.domain.model.loads.{Load, LoadGroup}
import org.euch.elevatorsim.domain.model.portal.Portal
import org.euch.elevatorsim.domain.model.shaft.{BaseShaft, Shaft}
import org.euch.elevatorsim.domain.model.transport.Transport
import org.euch.elevatorsim.domain.model.winch.Winch

import java.util.UUID

object Builder {

  val defaultLoadSpeedPPS = 25
  val defaultUnloadSpeedPPS = 25

  def buildLoadGroup(loads: List[(Load, Int)]): List[LoadGroup] = {
    loads
      .groupBy(_._2)
      .values
      .map { groupedLoad =>
        val destinationFloor = groupedLoad.head._2
        val name = Seq("lg", destinationFloor, UUID.randomUUID()).mkString("_")
        val elements = groupedLoad.map(_._1)
        LoadGroup(name, destinationFloor, elements, defaultLoadSpeedPPS, defaultUnloadSpeedPPS)
      }
      .toList
  }

  def buildShaft(
                  portals: List[Portal],
                  lowestFloorNumber: Int,
                  dimensions: DimensionsBox,
                  transport: Transport,
                  winch: Winch
                ): Shaft = {
    val name = "sh_" + UUID.randomUUID()
    BaseShaft(
      floors = buildFloors(portals, lowestFloorNumber),
      name,
      dimensions,
      transport,
      winch
    )
  }

  private def buildFloors(
                           portals: List[Portal],
                           lowestFloorNumber: Int
                         ): List[Floor] = {
    val groupedPortals = portals.groupBy(_.floorLevelZ).values
    val floorNumRange = scala.collection.mutable.Seq[Int]()
    for (i <- 0 to groupedPortals.size) {
      floorNumRange.last
    }


    assume(groupedPortals.size == floorNumRange.size)
    (groupedPortals zip floorNumRange).map { case (floorPortals, floorNumber) =>
      Floor(number = floorNumber, portals = floorPortals)
    }.toList
  }
}
