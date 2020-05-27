package it.unibo.yahm.client.sensors

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import it.unibo.yahm.client.utils.Location
import it.unibo.yahm.client.utils.SensorEvent
import java.util.concurrent.TimeUnit
import kotlin.math.abs


class SensorCombiners(reactiveLocation: ReactiveLocation, reactiveSensor: ReactiveSensor) {

    private val accelerometerObservable = reactiveSensor.observer(SensorType.LINEAR_ACCELERATION)
    private val gyroscopeObservable = reactiveSensor.observer(SensorType.GYROSCOPE)
    private val gpsObservable = reactiveLocation.observe()


    fun combineByStretchLength(minStretchLength: Double = 20.0): Observable<CombinedValues> {
        val subject = PublishSubject.create<CombinedValues>()
        val thread = Schedulers.newThread()

        var startLocation: Location? = null
        var lastLocation: Location? = null
        var length = 0.0
        val accelerationValues = mutableListOf<Acceleration>()
        val gyroscopeValues = mutableListOf<AngularVelocity>()

        // aggregate by stretches length
        val gpsDisposable = gpsObservable.subscribeOn(thread).subscribe {
            if (startLocation == null) {
                startLocation = it
            }
            if (lastLocation != null) {
                length += lastLocation!!.distanceTo(it)
            }
            lastLocation = it

            if (length > minStretchLength) {
                subject.onNext(
                    CombinedValues(
                        accelerationValues.toList(), gyroscopeValues.toList(),
                        GpsLocation.fromLocation(startLocation!!),
                        length, startLocation!!.time
                    )
                )
                accelerationValues.clear()
                gyroscopeValues.clear()
                startLocation = null
                lastLocation = null
                length = 0.0
            }
        }

        val accelerometerDisposable = accelerometerObservable.subscribeOn(thread).subscribe {
            accelerationValues.add(Acceleration.fromSensorEvent(it))
        }
        val gyroscopeDisposable = gyroscopeObservable.subscribeOn(thread).subscribe {
            gyroscopeValues.add(AngularVelocity.fromSensorEvent(it))
        }

        return subject.doOnDispose {
            println("Stopping subscribers for combineByStretchLength..")
            thread.shutdown()
            accelerometerDisposable.dispose()
            gyroscopeDisposable.dispose()
            gpsDisposable.dispose()
        }
    }

    fun combineByTime(timeSpan: Long = 20, timeSkip: Long? = null): Observable<CombinedValues> {
        val thread = Schedulers.newThread()
        val timedLocationSubscriber = TimedLocationSubscriber(gpsObservable)

        return Observable.combineLatest<SensorEvent, SensorEvent, Pair<SensorEvent, SensorEvent>>(
            accelerometerObservable.subscribeOn(thread),
            gyroscopeObservable.subscribeOn(thread),
            BiFunction { accelerometerEvent, gyroscopeEvent ->
                Pair(accelerometerEvent, gyroscopeEvent)
            }).filter { abs(it.first.timestamp - it.second.timestamp) < MAX_TIMESTAMP_DIFFERENCE }
            .buffer(timeSpan, timeSkip ?: timeSpan, TimeUnit.MILLISECONDS)
            .filter { it.isNotEmpty() }
            .map { pairs ->
                val accelerationValues = pairs.map { Acceleration.fromSensorEvent(it.first) }
                val gyroscopeValues = pairs.map { AngularVelocity.fromSensorEvent(it.second) }
                val timestamp = System.currentTimeMillis()

                val location = timedLocationSubscriber.locationAt(timestamp)
                CombinedValues(
                    accelerationValues, gyroscopeValues,
                    if (location != null) GpsLocation.fromLocation(location) else null,
                    null, timestamp
                )
            }.subscribeOn(thread)
            .doOnDispose {
                println("Stopping subscribers for combineByTime..")
                thread.shutdown()
                timedLocationSubscriber.dispose()
            }
    }

    companion object {
        private const val MAX_TIMESTAMP_DIFFERENCE = 10 * 1000 * 1000 // 10 millis
    }

}
