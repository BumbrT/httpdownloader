import day.kotlinhelper.*
import day.kotlinlib.*
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

/**
 * Created by a_day_000 on 16.08.2015.
 */
class DownloadedBytesCounterTest {
    fun processDownloadAndAssert(bytesPerTick : Long, totalTicks : Long, assertDownloaded: Long, assertArchived: LinkedList<Long>) {

        val counter = DownloadedBytesCounter()
        for(i in 1..totalTicks) {
            counter.addBytes(bytesPerTick)
        }
        println(counter.toString())
        assertEquals(assertDownloaded, counter.totalBytes)
        assertEquals(assertArchived, counter.archivedBytes)
    }
    Test fun runTest(): Unit {
        processDownloadAndAssert(100L, 1024, 1024L*100, linkedListOf<Long>() )
        processDownloadAndAssert(100L, 1024*1024, 1024L*1024*100, linkedListOf<Long>() )
        processDownloadAndAssert(100L*1024, 1024, 1024L*1024*100, linkedListOf<Long>() )
        processDownloadAndAssert(100L*1024, 1024*1024, 1024L*1024*1024*100, linkedListOf<Long>() )
        processDownloadAndAssert(100L, 1024L*1024*1024, 1024L*1024*1024*100, linkedListOf<Long>() )
        processDownloadAndAssert(100L*1024*1024, 1024, 1024L*1024*1024*100, linkedListOf<Long>() )

    }
    Test fun runCapacityTest(): Unit {
        processDownloadAndAssert(1048576L, 10000000L, 10485760000000, linkedListOf<Long>() )
        processDownloadAndAssert(1048576000000L, 10000000L, 1262387986432000000, linkedListOf<Long>(9223372013568000000) )
        processDownloadAndAssert(1048576000000L, 100000000L, 3400507850752000000, linkedListOf<Long>(9223372013568000000, 9223372013568000000,
                9223372013568000000, 9223372013568000000, 9223372013568000000, 9223372013568000000, 9223372013568000000,
                9223372013568000000, 9223372013568000000, 9223372013568000000, 9223372013568000000) )
    }

}