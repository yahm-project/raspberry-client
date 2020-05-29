package it.unibo.yahm.client.sensors


import com.pi4j.io.serial.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import it.unibo.yahm.client.utils.GpsData
import net.sf.marineapi.nmea.event.AbstractSentenceListener
import net.sf.marineapi.nmea.event.SentenceListener
import net.sf.marineapi.nmea.io.SentenceReader
import net.sf.marineapi.nmea.sentence.RMCSentence
import net.sf.marineapi.nmea.util.Position
import java.sql.Timestamp
import java.time.ZoneId


class ReactiveLocation {

    private var publishSubject: PublishSubject<GpsData>? = null
    private var sentenceReader: SentenceReader? = null
    private var sentenceListener: SentenceListener? = null

    init {
        val config = SerialConfig()
        val serial = SerialFactory.createInstance()
        config.device(SerialPort.getDefaultPort())
                .baud(Baud._9600)
                .dataBits(DataBits._8)
                .parity(Parity.NONE)
                .stopBits(StopBits._1)
                .flowControl(FlowControl.NONE)
        serial.open(config)

        sentenceReader = SentenceReader(serial.inputStream)
    }

    @Synchronized
    fun observe(): Observable<GpsData> {
        if (publishSubject == null) {
            publishSubject = PublishSubject.create()
            initListener(publishSubject!!)
        }
        return publishSubject!!
    }

    @Synchronized
    fun dispose() {
        sentenceReader?.removeSentenceListener(sentenceListener)
        sentenceReader = null
        sentenceListener = null
    }

    private fun initListener(publishSubject: PublishSubject<GpsData>) {
        sentenceListener = object : AbstractSentenceListener<RMCSentence>() {
            override fun sentenceRead(sentence: RMCSentence) {
                val pos: Position = sentence.position
                val dateTime = sentence.time.toDate(sentence.date.toDate())
                        .toInstant().atZone(ZoneId.of("CET")).toLocalDateTime()

                publishSubject.onNext(
                        GpsData(
                                latitude = pos.latitude.toFloat(),
                                longitude = pos.longitude.toFloat(),
                                accuracy = 0f,
                                speed = (sentence.speed * 0.514444).toFloat(), //knots to meters/s
                                time = Timestamp.valueOf(dateTime).time)
                )
            }
        }

        sentenceReader?.addSentenceListener(sentenceListener)
        sentenceReader?.start()
    }

}
