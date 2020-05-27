package it.unibo.yahm.client.entities

import it.unibo.yahm.client.utils.LatLng


data class Coordinate(
    val latitude: Float,
    val longitude: Float
) {
    fun toLatLng() = LatLng(latitude, longitude)
}
