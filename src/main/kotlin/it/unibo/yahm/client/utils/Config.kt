package it.unibo.yahm.client.utils

class Config {

    companion object {
        const val DEBUG = true
        const val CLASSIFIER_SERVICE_BASEURL = "http://localhost:8080"
        const val POTHOLE_SERVICE_DEVELOPMENT_BASEURL = "http://192.168.0.20"
        const val POTHOLE_SERVICE_PRODUCTION_BASEURL = "http://192.168.0.20"

        //raspberrypi config
        const val MPU6050_ADDR: Byte = 0x68
    }

}
