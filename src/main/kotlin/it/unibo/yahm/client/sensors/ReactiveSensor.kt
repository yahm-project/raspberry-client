package it.unibo.yahm.client.sensors

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import it.unibo.yahm.client.utils.Config
import it.unibo.yahm.client.utils.MPU6050
import it.unibo.yahm.client.utils.SensorEvent
import java.util.*
import java.util.concurrent.TimeUnit


class ReactiveSensor {

    private val publishSubjects = EnumMap<SensorType, PublishSubject<SensorEvent>>(SensorType::class.java)
    private val readerDisposables = EnumMap<SensorType, Disposable?>(SensorType::class.java)
    private val thread = Schedulers.newThread()

    private val mpu6050 = MPU6050(Config.MPU6050_ADDR)

    @Synchronized
    fun observer(sensorType: SensorType): Observable<SensorEvent> =
            if (publishSubjects.containsKey(sensorType)) {
                publishSubjects[sensorType]!!
            } else {
                val observable = createObserver(sensorType)
                publishSubjects[sensorType] = observable
                observable
            }

    private fun createObserver(sensorType: SensorType): PublishSubject<SensorEvent> {
        val subject = PublishSubject.create<SensorEvent>()

        val readerDisposable = thread.schedulePeriodicallyDirect({
            val values = when (sensorType) {
                SensorType.LINEAR_ACCELERATION -> mpu6050.getAccData()
                SensorType.GYROSCOPE -> mpu6050.getGyroData()
                else -> null
            }

            if (values != null) {
                subject.onNext(SensorEvent(values.map { it.toFloat() }.toFloatArray(), System.currentTimeMillis()))
            }
        }, 0, 20, TimeUnit.MILLISECONDS)
        readerDisposables[sensorType] = readerDisposable

        return subject
    }

    fun dispose(sensorType: SensorType) {
        readerDisposables[sensorType]?.dispose()
        thread.shutdown()
    }

}
