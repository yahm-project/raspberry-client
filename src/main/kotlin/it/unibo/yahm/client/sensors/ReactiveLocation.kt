package it.unibo.yahm.client.sensors


import com.pi4j.io.serial.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import it.unibo.yahm.client.utils.GpsData
import net.sf.marineapi.nmea.event.AbstractSentenceListener
import net.sf.marineapi.nmea.event.SentenceListener
import net.sf.marineapi.nmea.io.SentenceReader
import net.sf.marineapi.nmea.sentence.GGASentence
import net.sf.marineapi.nmea.util.Position
import java.io.IOException
import java.sql.Timestamp


class ReactiveLocation(
        private val minDistance: Float = 0.0f,
        private val minTime: Long = 100
) {

    private var publishSubject: PublishSubject<GpsData>? = null
    private val serial: Serial = SerialFactory.createInstance()
    private var sentenceReader: SentenceReader? = null
    private var sentenceListener: SentenceListener? = null

    init {
        try {
            var config = SerialConfig()
            config.device(SerialPort.getDefaultPort())
                    .baud(Baud._9600)
                    .dataBits(DataBits._8)
                    .parity(Parity.NONE)
                    .stopBits(StopBits._1)
                    .flowControl(FlowControl.NONE)
            serial.open(config)
            sentenceReader = SentenceReader(serial.inputStream)
        } catch (e: IOException) {
            println("An error occurred during serial initialization.")
        }
    }

    @Synchronized
    fun observe(): Observable<GpsData> {
        if (publishSubject == null) {
            publishSubject = PublishSubject.create<GpsData>()
            initListener(publishSubject)
        }
        return publishSubject!!
    }

    private fun initListener(publishSubject: PublishSubject<GpsData>?) {

        sentenceListener = object : AbstractSentenceListener<GGASentence>() {
            override fun sentenceRead(gga: GGASentence) {
                val pos: Position = gga.position
                publishSubject?.onNext(
                        GpsData(
                                latitude = pos.latitude.toFloat(),
                                longitude = pos.longitude.toFloat(),
                                accuracy = gga.horizontalDOP.toFloat(),
                                speed = 0f,
                                time = Timestamp(System.currentTimeMillis()).time)
                )
            }
        }
        sentenceReader?.addSentenceListener(sentenceListener)
        sentenceReader?.start()
    }

    @Synchronized
    fun dispose() {
        sentenceReader?.removeSentenceListener(sentenceListener)
    }

}
