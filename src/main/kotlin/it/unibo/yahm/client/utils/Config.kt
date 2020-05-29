package it.unibo.yahm.client.utils

class Config {

    companion object {
        const val DEBUG = true
        const val POTHOLE_SERVICE_DEVELOPMENT_BASEURL = "192.168.0.20"
        const val POTHOLE_SERVICE_PRODUCTION_BASEURL = "192.168.0.20"

        //raspberrypi config
        const val MPU6050_ADDR: Byte = 0x68
        //const val TX_PIN: Int = 14
        //const val RX_PIN: Int = 15
    }

}
