/**
* Created by a_day_000 on 08.08.2015.
*/
import day.kotlinhelper.*
import day.kotlinlib.*
import org.junit.Test;
import kotlin.reflect.jvm.java
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.failsWith

public class DownloadPortionTest {
    val portion : DownloadPortion = SimpleDownloadPortion(64 * FileSize.K.bytes, TimeSpan.S.millis)
    fun shouldFailInInitialization ( size : Long, time : Long) {
        failsWith(ExceptionInInitializerError::class.java, { SimpleDownloadPortion(size, time) })
    }
    fun throttleIntervalShouldBe(fileSize : Int, fileSizeMultiplier : FileSize, compareFunc : (Long) -> Boolean  ) {
        assertTrue("wrong throttling", {
            compareFunc(portion.getThrottleSleepInterval(fileSize * fileSizeMultiplier.bytes.toInt(), TimeSpan.S.millis))
        } )
    }

    fun throttleIntervalShouldEquals(fileSize : Int, fileSizeMultiplier : FileSize, expect : Long , timeSpan : TimeSpan = TimeSpan.S ) {
        val time = portion.getThrottleSleepInterval(fileSize * fileSizeMultiplier.bytes.toInt() , timeSpan.millis)

        assertTrue("${time} expected ${expect} ", {
            time == expect
        } )
    }
    Test fun shouldThrottle(): Unit {

        shouldFailInInitialization(Int.MAX_VALUE.toLong() / 2 +1 , 100)
        shouldFailInInitialization(Int.MAX_VALUE.toLong() *10  / 2 +5 , 1000)
        shouldFailInInitialization(89 , 1000)
        shouldFailInInitialization(8 , 100)
        throttleIntervalShouldBe(65,FileSize.K, { l -> l > 0} )
        throttleIntervalShouldBe(63,FileSize.M, { l -> l > 0} )
        throttleIntervalShouldBe(1,FileSize.G, { l -> l > 0} )
        throttleIntervalShouldBe(63,FileSize.K, { l -> l == 0L} )
        throttleIntervalShouldBe(1,FileSize.Byte, { l -> l == 0L} )


        throttleIntervalShouldBe(65,FileSize.K, { l -> l > 0} )

        throttleIntervalShouldEquals(128, FileSize.K, 50L)
        throttleIntervalShouldEquals(256, FileSize.K, 75L)
        throttleIntervalShouldEquals(85, FileSize.K, 25L)
        throttleIntervalShouldEquals(1, FileSize.G, 100L)
        throttleIntervalShouldEquals(1, FileSize.G, 100L, TimeSpan.Millisecond)


    }
}