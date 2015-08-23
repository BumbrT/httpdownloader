package day.kotlinlib


import day.kotlinhelper.oneIfZero
import java.util.*

/**
 * Created by a_day_000 on 04.08.2015.
 */
/**
 * represents portion (bytes per interval) for download thread, each thread loops download cycle with allowed bytes less than provided portion
 * on each iterations requests getThrottleSleepInterval
 */
interface DownloadPortion {
    var bytesPerPortion: Int
        get
    fun getThrottleSleepInterval(downloadedBytes: Int, elapsedMilis : Long ) : Long
    val millisecondsPerPortion: Long
}
/**
 * Download portion, with 100 ms base, error free initialization logic, and simle throttle counter
 */
class SimpleDownloadPortion(val bytes: Long, val milliseconds: Long) : DownloadPortion {

    override  val millisecondsPerPortion: Long = 100L

    override var bytesPerPortion: Int = 0
        private set

    private val totalPortions: Long =  milliseconds / millisecondsPerPortion
    init {
        if (bytes == 0L)
            throw ExceptionInInitializerError("should acquire more than 0 bytes")

        if (milliseconds == 0L)
            throw ExceptionInInitializerError("portion milliseconds must be more then 0")

        if (totalPortions == 0L)
            throw ExceptionInInitializerError("portion milliseconds must be more then ${millisecondsPerPortion} milliseconds")
        val longBytesPerPortion = oneIfZero((bytes / totalPortions))
        if (longBytesPerPortion > Int.MAX_VALUE/ x2BufferSizeRezervation) // x2 - for bandwidth reservation
            throw ExceptionInInitializerError("buffer size shoid be less or equals then ${Int.MAX_VALUE/ x2BufferSizeRezervation} , try to reduce download speed per thread")
        if (longBytesPerPortion < 9L) // to avoid task prioritization conflicts, when slow task speed = 0
            throw ExceptionInInitializerError("buffer size shoid be more or equals then 90 bytes per second, try to increase download speed per thread")
        bytesPerPortion = longBytesPerPortion.toInt()

    }
    var bytesPerMillisecond = bytesPerPortion / millisecondsPerPortion

   override fun getThrottleSleepInterval(downloadedBytes: Int, elapsedMilis : Long ) : Long {
        val dlBandwidth = oneIfZero(downloadedBytes / oneIfZero(elapsedMilis));
        val sleepInterval = this.millisecondsPerPortion -  this.bytesPerPortion / dlBandwidth
        if (sleepInterval < 0L)
            return 0L
        return sleepInterval
    }
}
