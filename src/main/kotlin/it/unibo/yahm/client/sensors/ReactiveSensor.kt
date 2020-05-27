package it.unibo.yahm.client.sensors

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import it.unibo.yahm.client.utils.SensorEvent
import java.util.*


class ReactiveSensor() {

    private val publishSubjects = EnumMap<SensorType, PublishSubject<SensorEvent>>(SensorType::class.java)

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
        return subject
    }

    fun dispose(sensorType: SensorType) {
    }

}
