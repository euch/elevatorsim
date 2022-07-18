package org.euch.elevatorsim.simulation.order.router

sealed trait RouteDirection

object RouteDirection {
  case object Up extends RouteDirection

  case object Stop extends RouteDirection

  case object Down extends RouteDirection
}
