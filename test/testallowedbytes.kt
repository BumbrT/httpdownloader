import day.kotlinhelper.*
import day.kotlinlib.*
import org.junit.Test
import kotlin.reflect.jvm.java
import kotlin.test.assertTrue
import kotlin.test.fails
import kotlin.test.failsWith

/**
 * Created by a_day_000 on 09.08.2015.
 */
public class AllowedBytesTest {
   // val portion: DownloadPortion = SimpleDownloadPortion(64 * FileSize.K.bytes, TimeSpan.S.millis)
    fun bytesShouldNotBeNullForNotDeadStates( x : DownloadPortion) {
        for (i in TaskState.notDeadStates) {
            val allowed =  i.allowedBytesToDownload(x.bytesPerPortion)
            assertTrue("wrong bytes for state ${i} and bytes protion ${x.bytesPerPortion}", {
                allowed > 0
            })
            println("allowed ${allowed} for state ${i} and bytes ${x.bytesPerPortion} ")
        }
    }
    Test fun runTest(): Unit {
        bytesShouldNotBeNullForNotDeadStates( SimpleDownloadPortion( 90L, TimeSpan.S.millis) );

        bytesShouldNotBeNullForNotDeadStates( SimpleDownloadPortion(FileSize.K.bytes, TimeSpan.S.millis) );

        bytesShouldNotBeNullForNotDeadStates( SimpleDownloadPortion(64 * FileSize.K.bytes, TimeSpan.S.millis) );
        bytesShouldNotBeNullForNotDeadStates( SimpleDownloadPortion(FileSize.G.bytes, TimeSpan.S.millis) );

        bytesShouldNotBeNullForNotDeadStates( SimpleDownloadPortion(2 * FileSize.G.bytes, TimeSpan.S.millis) );

        bytesShouldNotBeNullForNotDeadStates( SimpleDownloadPortion(4 * FileSize.G.bytes, TimeSpan.S.millis) );

        bytesShouldNotBeNullForNotDeadStates( SimpleDownloadPortion( Int.MAX_VALUE.toLong() / 2, 100) );

  //      shouldFailInInitialization( SimpleDownloadPortion( Int.MAX_VALUE.toLong() / 2 +1 , 100) );

    }
}