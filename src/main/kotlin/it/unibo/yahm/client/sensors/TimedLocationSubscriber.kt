package it.unibo.yahm.client.sensors

import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import it.unibo.yahm.client.utils.Location
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.math.abs


class TimedLocationSubscriber(gpsObservable: Observable<Location>, maxQueueSize: Int = 4096) {

    private val queue = ConcurrentLinkedDeque<Location>()
    private val thread = Schedulers.newThread()
    private val disposable: Disposable

    private var lastLocation: Location? = null

    init {
        disposable = gpsObservable.subscribeOn(thread).subscribe {
            queue.push(it)
            if (queue.size > maxQueueSize) {
                queue.pop()
            }
        }
    }

    @Synchronized
    fun locationAt(timestamp: Long): Location? {
        if (lastLocation != null) {
            val firstLocation = queue.peekFirst() ?: return lastLocation

            if (abs(lastLocation!!.time - timestamp) < abs(firstLocation.time - timestamp)) {
                return lastLocation
            }
        }

        var firstLocation = queue.pollFirst()
        while (firstLocation != null) {
            val nextLocation = queue.peekFirst()
            if (nextLocation == null ||
                abs(firstLocation.time) - timestamp < abs(nextLocation.time - timestamp)
            ) {
                lastLocation = firstLocation
                return firstLocation
            }
            firstLocation = queue.poll()
        }

        return null
    }

    @Synchronized
    fun dispose() {
        println("Stopping subscribers..")
        thread.shutdown()
        disposable.dispose()
    }

}
