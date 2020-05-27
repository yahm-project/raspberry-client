package it.unibo.yahm.client.utils

import kotlin.math.*

class MapUtils {
    companion object {
        private const val EARTH_RADIUS = 3958.75
        private const val METER_CONVERSION = 1609

        /** distance in meters  */
        fun distBetween(
            pointA: LatLng,
            pointB: LatLng
        ): Float {
            val dLat = Math.toRadians((pointB.latitude - pointA.latitude).toDouble())
            val dLng = Math.toRadians((pointB.longitude - pointA.longitude).toDouble())
            val a = (sin(dLat / 2) * sin(dLat / 2)
                    + (cos(Math.toRadians(pointA.latitude.toDouble()))
                    * cos(Math.toRadians(pointB.latitude.toDouble())) * sin(dLng / 2)
                    * sin(dLng / 2)))
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            val dist = EARTH_RADIUS * c

            return (dist * METER_CONVERSION).toFloat()
        }
    }
}