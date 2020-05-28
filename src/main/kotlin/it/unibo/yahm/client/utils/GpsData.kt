package it.unibo.yahm.client.utils


class GpsData(
        val latitude: Float,
        val longitude: Float,
        val time: Long,
        val accuracy: Float?,
        val speed: Float?
) {
    fun distanceTo(gpsData: GpsData): Float {
        return MapUtils.distBetween(LatLng(latitude, longitude), LatLng(gpsData.latitude, gpsData.longitude))
    }
}