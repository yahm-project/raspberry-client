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
import net.sf.marineapi.nmea.util.Time
import java.io.IOException
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*


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

    fun utcToCet(timeInUtc: LocalDateTime?): LocalDateTime? {
        val utcTimeZoned = ZonedDateTime.of(timeInUtc, ZoneId.of("UTC"))
        return utcTimeZoned.withZoneSameInstant(ZoneId.of("CET")).toLocalDateTime()
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

        sentenceListener = object : AbstractSentenceListener<RMCSentence>() {
            override fun sentenceRead(sentence: RMCSentence) {
                val pos: Position = sentence.position
                val time: Time = sentence.time
                val time2: Date = time.toDate(sentence.date.toDate())
                val time3 = time2.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime()
                val time4 = utcToCet(time3).toString().toLong()
                publishSubject?.onNext(
                        GpsData(
                                latitude = pos.latitude.toFloat(),
                                longitude = pos.longitude.toFloat(),
                                accuracy = 0f,
                                speed = (sentence.speed * 0.514444).toFloat(), //knots to meters/s
                                time = time4)
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
