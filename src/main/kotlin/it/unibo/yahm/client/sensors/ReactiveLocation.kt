package it.unibo.yahm.client.sensors


import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import it.unibo.yahm.client.utils.Location


class ReactiveLocation(
    private val minDistance: Float = 0.0f,
    private val minTime: Long = 100
) {

    private var publishSubject: PublishSubject<Location>? = null

    @Synchronized
    fun observe(): Observable<Location> {
        if (publishSubject == null) {
            publishSubject = createObserver()
        }
        return publishSubject!!
    }

    private fun createObserver(): PublishSubject<Location> {
        val subject = PublishSubject.create<Location>()

        return subject
    }

    @Synchronized
    fun dispose() {
    }

}
