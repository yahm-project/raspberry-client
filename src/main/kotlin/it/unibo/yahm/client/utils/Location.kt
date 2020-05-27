package it.unibo.yahm.client.utils


class Location(
        val longitude: Float,
        val latitude: Float,
        val time: Long,
        val accuracy: Float?,
        val speed: Float?
) {
    fun distanceTo(location: Location): Float {
        return MapUtils.distBetween(LatLng(latitude, longitude), LatLng(location.latitude, location.longitude))
    }
}