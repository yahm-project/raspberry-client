package it.unibo.yahm

import it.unibo.yahm.client.sensors.ReactiveLocation
import it.unibo.yahm.client.sensors.ReactiveSensor
import it.unibo.yahm.client.services.RetrofitService
import it.unibo.yahm.client.services.RoadClassifiersService


fun main() {
    val reactiveSensor = ReactiveSensor()
    val reactiveLocation = ReactiveLocation()
    val retrofitService = RetrofitService()
    val roadClassifierService = RoadClassifiersService(reactiveSensor, reactiveLocation, retrofitService.spotholeService)

    roadClassifierService.startService()
}
