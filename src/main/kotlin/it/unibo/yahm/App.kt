package it.unibo.yahm

import it.unibo.yahm.client.sensors.ReactiveLocation
import it.unibo.yahm.client.sensors.ReactiveSensor
import it.unibo.yahm.client.services.RetrofitService
import it.unibo.yahm.client.services.RoadClassifiersService
import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger


fun main() {
    val logger = Logger.getLogger("yahm")
    val handlerObj = ConsoleHandler()
    handlerObj.level = Level.ALL;
    logger.addHandler(handlerObj)
    logger.level = Level.ALL
    logger.useParentHandlers = false

    val reactiveSensor = ReactiveSensor()
    val reactiveLocation = ReactiveLocation()
    val retrofitService = RetrofitService()
    val roadClassifierService = RoadClassifiersService(reactiveSensor, reactiveLocation, retrofitService.spotholeService)

    roadClassifierService.startService()
}
