package day.kotlinlib

import com.lmax.disruptor.EventFactory
import com.lmax.disruptor.EventHandler
import com.lmax.disruptor.RingBuffer
import com.lmax.disruptor.dsl.Disruptor
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.logging.Logger

/**
 * Created by a_day_000 on 09.08.2015.
 */

/**
 * schedules tasks, receives messages from threads, binds to TaskEvent.subscribe
 * uses DownloadSchedulerStateSnapshot for representing internal state
 *
 */
class DownloadScheduler(val plan: DownloadPlan, val logger : Logger) {
    private val pendingTasks = plan.pendingTasks // changes only in DownloadSchedulerStateSnapshot
    private val allowedThreadsCount : Int = plan.threadsCount // free slots for new threads
    private val totalBandwidthInStatePoints : Int = allowedThreadsCount * TaskState.NORMAL.value
    private val startedTasks = hashMapOf<Int, TaskState>()
    //val fastTasks = linkedListOf<Int>()
    //val workFinished : Boolean = false
    val bytesCounter = DownloadedBytesCounter()
    private var allocatedBandwidth = 0
      private fun recalculateDigest() {
        var bandwidth = 0
        startedTasks.values().forEach { value -> bandwidth += value.value  }
        allocatedBandwidth = bandwidth
    }
    public fun hasPendingTasks() : Boolean {
        return pendingTasks.lastIndex >= 0
    }
    /**
     *  representing internal state
     */
    inner class  DownloadSchedulerStateSnapshot() {

        val freeBandwidth = totalBandwidthInStatePoints - allocatedBandwidth
        fun popNextTasks(alreadyRunningThreadsCount : Int) : List<DownloadTask>{
            var freeThreads = allowedThreadsCount - alreadyRunningThreadsCount;
            if (freeThreads > pendingTasks.count())
                freeThreads = pendingTasks.count()
            if (freeThreads <= 0)
                return listOf()
            val bandwidthPerThread = freeBandwidth / freeThreads
            val allowedStateByBandwith = TaskState.getAllowedStateForNewTask(bandwidthPerThread)
            if (allowedStateByBandwith == null)
                return listOf()
            val nextTasks = linkedListOf<DownloadTask>()
            (1..freeThreads).forEach {
                val task = pendingTasks.pop()
                task.startState = allowedStateByBandwith
                nextTasks.push(task)
            }
            return nextTasks
        }
    }
    fun OnCreated (state : IdentifiedTaskState) {
        logger.log(traceLogLevel,"${state.threadId} reported create")
        startedTasks.put(state.threadId, state.threadState )
        recalculateDigest()
    }
    fun OnStateChanged (state : IdentifiedTaskState) {
        logger.log(traceLogLevel,"${state.threadId} reported change state to ${state.threadState}")
        if (startedTasks.containsKey(state.threadId))
            startedTasks[state.threadId] = state.threadState
        recalculateDigest()
    }
    fun OnDownloadedBytes  (bytes : Int) {
        bytesCounter.addBytes(bytes)
    }
    init {
        val action = TaskEvent.EventAction(
                { state -> this.OnCreated(state) },
                { state -> this.OnStateChanged(state) },
                {bytes -> this.OnDownloadedBytes(bytes)}
                )
        TaskEvent.subscribe(action)
    }



}
