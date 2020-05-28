package it.unibo.yahm.client.utils

/**
 * This Class implements a MPU6050 accelerometer and gyroscope for use on i2c communications with a raspberry pi.
 * V1.0 Modified from MPU6050 code from: MP
 * MPU6050 data sheet found at https://www.invensense.com/wp-content/uploads/2015/02/MPU-6000-Register-Map1.pdf
 */
import com.pi4j.io.i2c.I2CBus
import com.pi4j.io.i2c.I2CDevice
import com.pi4j.io.i2c.I2CFactory


class MPU6050 {
    private var i2cBus: I2CBus? = null
    private var device: I2CDevice? = null

    /**
     * This is a constructor that initializes a new MPU6050 with the passed in address. This assumes that you are using a RasPi2 or newer.
     * @param address The I2C address of the MPU6050.
     */
    constructor(address: Byte) {
        init(address, 1)
    }

    /**
     * This is a constructor that initializes a new MPU6050 with the passed in address.
     * @param address i2c address of the MPU6050. Usually 0x68 or 0x69.
     * @param busNo Bus number that will be used. 0 for RasPi1 1 for all other RasPi
     */
    constructor(address: Byte, busNo: Int) {
        init(address, busNo)
    }

    @Throws(Exception::class)
    private fun init(address: Byte, busNo: Int) {
        i2cBus = I2CFactory.getInstance(busNo)
        device = i2cBus!!.getDevice(address.toInt())
        // Wake up the MPU6050 from sleep since it starts in sleep mode by default.
        device!!.write(PWR_MGMT_1.toInt(), 0x00)
    }
    //General i2c Communication Methods
    /**
     * Reads a word (16 bits) from the passed register and the register after. The data is combine.
     * @param register Place that is being read from.
     * @return The value of the word read from the register.
     * @throws Exception FIXME passes all errors onto the surrounding program.
     */
    @Throws(Exception::class)
    fun readI2CWord(register: Int): Int {
        val high: Int = device!!.read(register)
        val low: Int = device!!.read(register + 1)
        val value = (high shl 8) + low
        return if (value >= 0x8000) {
            -(65535 - value + 1)
        } else {
            value
        }
    }

    //MPU6050 Methods
    /**
     * Reads the temperature from the MPU6050's onboard sensor.
     * @return The temperature in degrees Celsius.
     * @throws Exception FIXME passes all errors onto the surrounding program.
     */
    @Throws(Exception::class)
    fun getTemp(): Double {
        val rawTemp = readI2CWord(TEMP_OUT0)
        return rawTemp / 340 + 36.53
    }

    /**
     * Sets the range of the accelerometer.
     * @param accRange The range that the accelerometer is set to. Using one of the predefined ranges is advised.
     * @throws Exception FIXME passes all errors onto the surrounding program.
     */
    @Throws(Exception::class)
    fun setAccRange(accRange: Byte) {
        device!!.write(ACCEL_CONFIG, 0x00)
        device!!.write(ACCEL_CONFIG, accRange)
    }

    /**
     * Reads the raw accelerometer range.
     * @return The raw value from the ACCEL_COMFIG register.
     * @throws Exception FIXME passes all errors onto the surrounding program.
     */
    @Throws(Exception::class)
    fun readRawAccelRange(): Double {
        return device!!.read(ACCEL_CONFIG).toDouble()
    }

    /**
     * Reads the accelerometer range in terms of gravity.
     * @return Returns an integer: -1, 2, 4, 8, 16. If -1 an error occurred.
     * @throws Exception FIXME passes all errors onto the surrounding program.
     */
    @Throws(Exception::class)
    fun readAccRange(): Int {
        return when (readRawAccelRange()) {
            ACCEL_RANGE_2G.toDouble() -> {
                2
            }
            ACCEL_RANGE_4G.toDouble() -> {
                4
            }
            ACCEL_RANGE_8G.toDouble() -> {
                8
            }
            ACCEL_RANGE_16G.toDouble() -> {
                16
            }
            else -> {
                -1
            }
        }
    }
    /**
     * Gets the x, y, and z accelerometer data.
     * @param g If true: returned values are in terms of gravity(g). If false: returned values are in terms of m/s^2.
     * @return Returns x, y, z in a double array [x, y, z].
     * @throws Exception FIXME passes all errors onto the surrounding program.
     */
    /**
     * Gets the x, y, and z accelerometer data.
     * @return Returns x, y, z in a double array [x, y, z]. All values are in m/s^2.
     * @throws Exception FIXME passes all errors onto the surrounding program.
     */
    @JvmOverloads
    @Throws(Exception::class)
    fun getAccData(g: Boolean = false): DoubleArray {
        var x = readI2CWord(ACCEL_XOUT0).toDouble()
        var y = readI2CWord(ACCEL_YOUT0).toDouble()
        var z = readI2CWord(ACCEL_ZOUT0).toDouble()
        val accScaleModifier: Double
        val accRange = readRawAccelRange()
        accScaleModifier = when (accRange) {
            ACCEL_RANGE_2G.toDouble() -> {
                ACCEL_SCALE_MODIFIER_2G
            }
            ACCEL_RANGE_4G.toDouble() -> {
                ACCEL_SCALE_MODIFIER_4G
            }
            ACCEL_RANGE_8G.toDouble() -> {
                ACCEL_SCALE_MODIFIER_8G
            }
            ACCEL_RANGE_16G.toDouble() -> {
                ACCEL_SCALE_MODIFIER_16G
            }
            else -> {
                println("Unkown range - accel_scale_modifier set to self.ACCEL_SCALE_MODIFIER_2G\"")
                ACCEL_SCALE_MODIFIER_2G
            }
        }
        x /= accScaleModifier
        y /= accScaleModifier
        z /= accScaleModifier
        if (!g) {
            x *= GRAVITIY_MS2
            y *= GRAVITIY_MS2
            z *= GRAVITIY_MS2
        }
        return doubleArrayOf(x, y, z)
    }

    /**
     * Sets the range of the Gyroscope.
     * @param gyroRange The range to set the gyroscope to. Using a predefined range is advised.
     * @throws Exception FIXME passes all errors onto the surrounding program.
     */
    @Throws(Exception::class)
    fun setGyroRange(gyroRange: Byte) {
        device!!.write(GYRO_CONFIG, 0x00.toByte())
        device!!.write(GYRO_CONFIG, gyroRange)
    }

    /**
     * Reads the raw range of the gyroscope.
     * @return Returns the raw value from the GYRO_CONFIG register.
     * @throws Exception FIXME passes all errors onto the surrounding program.
     */
    @Throws(Exception::class)
    fun readRawGyroRange(): Double {
        return device!!.read(GYRO_CONFIG).toDouble()
    }

    /**
     * Reads the range of the gyroscope.
     * @return Returns an integer: -1, 250, 500, 1000, and 2000. If -1 is returned something bad has happened.
     * @throws Exception FIXME passes all errors onto the surrounding program.
     */
    @Throws(Exception::class)
    fun readGyroRange(): Double {
        return when (readRawGyroRange()) {
            GYRO_RANGE_250DEG.toDouble() -> {
                250.0
            }
            GYRO_RANGE_500DEG.toDouble() -> {
                500.0
            }
            GYRO_RANGE_1000DEG.toDouble() -> {
                1000.0
            }
            GYRO_RANGE_2000DEG.toDouble() -> {
                2000.0
            }
            else -> {
                (-1).toDouble()
            }
        }
    }

    /**
     * Gets the data from the gyroscope.
     * @return Returns the values as a double array: [x, y, z].
     * @throws Exception FIXME passes all errors onto the surrounding program.
     */
    @Throws(Exception::class)
    fun getGyroData(): DoubleArray {
        var x = readI2CWord(GYRO_XOUT0.toInt()).toDouble()
        var y = readI2CWord(GYRO_YOUT0.toInt()).toDouble()
        var z = readI2CWord(GYRO_ZOUT0.toInt()).toDouble()
        val gyroScaleModifier: Double
        val gyroRange = readRawGyroRange()
        gyroScaleModifier = when (gyroRange) {
            GYRO_RANGE_250DEG.toDouble() -> {
                GYRO_SCALE_MODIFIER_250DEG
            }
            GYRO_RANGE_500DEG.toDouble() -> {
                GYRO_SCALE_MODIFIER_500DEG
            }
            GYRO_RANGE_1000DEG.toDouble() -> {
                GYRO_SCALE_MODIFIER_1000DEG
            }
            GYRO_RANGE_2000DEG.toDouble() -> {
                GYRO_SCALE_MODIFIER_2000DEG
            }
            else -> {
                println("Unknown range - gyro_scale_modifier set to self.GYRO_SCALE_MODIFIER_250DEG")
                GYRO_SCALE_MODIFIER_250DEG
            }
        }
        x /= gyroScaleModifier
        y /= gyroScaleModifier
        z /= gyroScaleModifier
        return doubleArrayOf(x, y, z)
    }

    companion object {
        //Global Variables
        private const val GRAVITIY_MS2 = 9.80665
        //Scale Modifiers
        private const val ACCEL_SCALE_MODIFIER_2G = 16384.0
        private const val ACCEL_SCALE_MODIFIER_4G = 8192.0
        private const val ACCEL_SCALE_MODIFIER_8G = 4096.0
        private const val ACCEL_SCALE_MODIFIER_16G = 2048.0
        private const val GYRO_SCALE_MODIFIER_250DEG = 131.0
        private const val GYRO_SCALE_MODIFIER_500DEG = 65.5
        private const val GYRO_SCALE_MODIFIER_1000DEG = 32.8
        private const val GYRO_SCALE_MODIFIER_2000DEG = 16.4
        // Pre-defined Ranges
        var ACCEL_RANGE_2G: Int = 0x00
        var ACCEL_RANGE_4G: Int = 0x08
        var ACCEL_RANGE_8G: Int = 0x10
        var ACCEL_RANGE_16G: Int = 0x18
        var GYRO_RANGE_250DEG: Int = 0x00
        var GYRO_RANGE_500DEG: Int = 0x08
        var GYRO_RANGE_1000DEG: Int = 0x10
        var GYRO_RANGE_2000DEG: Int = 0x18
        // MPU-6050 Registers
        private const val PWR_MGMT_1: Int = 0x6B
        private const val PWR_MGMT_2: Int = 0x6C
        private const val ACCEL_XOUT0: Int = 0x3B
        private const val ACCEL_YOUT0: Int = 0x3D
        private const val ACCEL_ZOUT0: Int = 0x3F
        private const val TEMP_OUT0: Int = 0x41
        private const val GYRO_XOUT0: Int = 0x43
        private const val GYRO_YOUT0: Int = 0x45
        private const val GYRO_ZOUT0: Int = 0x47
        private const val ACCEL_CONFIG: Int = 0x1C
        private const val GYRO_CONFIG: Int = 0x1B
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                val mpu = MPU6050(0x68)
                println("MPU Temp: " + mpu.getTemp())
                val accData = mpu.getAccData()
                println("MPU Acc x: " + accData[0])
                println("MPU Acc y: " + accData[1])
                println("MPU Acc z: " + accData[2])
                val gyroData = mpu.getGyroData()
                println("MPU Gyro x: " + gyroData[0])
                println("MPU Gyro y: " + gyroData[1])
                println("MPU Gyro z: " + gyroData[2])
            } catch (e: Exception) {
                println(e)
            }
        }
    }
}