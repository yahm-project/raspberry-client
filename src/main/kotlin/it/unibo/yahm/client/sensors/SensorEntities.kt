package it.unibo.yahm.client.sensors

import it.unibo.yahm.client.entities.Coordinate
import it.unibo.yahm.client.entities.Quality
import it.unibo.yahm.client.utils.GpsData
import it.unibo.yahm.client.utils.SensorEvent


data class Acceleration(
    val x: Double,
    val y: Double,
    val z: Double
) {
    companion object {
        fun fromSensorEvent(sensorEvent: SensorEvent): Acceleration =
            Acceleration(sensorEvent.values[0].toDouble(), sensorEvent.values[1].toDouble(),
                sensorEvent.values[2].toDouble())
    }
}

data class AngularVelocity(
    val x: Double,
    val y: Double,
    val z: Double
) {
    companion object {
        fun fromSensorEvent(sensorEvent: SensorEvent): AngularVelocity =
            AngularVelocity(sensorEvent.values[0].toDouble(), sensorEvent.values[1].toDouble(),
                sensorEvent.values[2].toDouble())

    }
}

data class GpsLocation(
        val latitude: Float,
        val longitude: Float,
        val accuracy: Float?,
        val speed: Float?,
        val time: Long
) {

    companion object {
        fun fromLocation(location: GpsData): GpsLocation = GpsLocation(
            location.latitude,
            location.longitude,
            location.accuracy,
                location.speed,
            location.time
        )
    }
}

data class CombinedValues(
    val accelerationValues: List<Acceleration>,
    val gyroscopeValues: List<AngularVelocity>,
    val location: GpsLocation?,
    val length: Double?,
    val timestamp: Long
)

data class StretchQuality(
    val position: Coordinate,
    val timestamp: Long,
    val radius: Double,
    val quality: Quality
)
