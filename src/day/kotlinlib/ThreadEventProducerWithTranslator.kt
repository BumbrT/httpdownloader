package day.kotlinlib

import com.lmax.disruptor.EventTranslatorOneArg
import com.lmax.disruptor.EventTranslatorTwoArg
import com.lmax.disruptor.RingBuffer


/**
 * Disruptor classes for cross thread messaging without blocking
  */
/**
 * contains message data
 */
data class IdentifiedTaskState(val threadId: Int, val threadState: TaskState, val isCreated: Boolean = false)



/**
 * produces and translates messages across threads
 */
class ThreadEventProducerWithTranslator(val ringBuffer: RingBuffer<TaskEvent>) {
    private object TRANSLATOR : EventTranslatorTwoArg<TaskEvent, IdentifiedTaskState, Int> {
        override fun translateTo(event: TaskEvent?, sequence: Long, state: IdentifiedTaskState?, bytes: Int?) {
            event?.stateChanged = state
            event?.bytesDownloaded = bytes

        }
    }

    fun submitCreated(threadId: Int, state: TaskState) {

        ringBuffer.publishEvent(TRANSLATOR, IdentifiedTaskState(threadId, state, true), null)
    }

    fun submitStateChanged(threadId: Int, state: TaskState) {
        ringBuffer.publishEvent(TRANSLATOR, IdentifiedTaskState(threadId, state, false), null)
    }

    fun submitDownloadedBytes(msg: Int) {
        ringBuffer.publishEvent(TRANSLATOR, null, msg)
    }
}