package day.kotlinlib

import java.util.concurrent.Future

/**
 * Created by a_day_000 on 23.08.2015.
 */

/**
 * represent a task in DownloadExecutor
 */
data class ScheduledDownloadTask(val thread: Future<*>, val downloadThread: DownloadThread) {

    val isSpeedupRequested: Boolean
    get() {
        return  !thread.isDone()  && !thread.isCancelled()
                && downloadThread.stateManager.isSpeedUpRequired
                && downloadThread.currentTaskState.aliveAndHasNextState()
    }

    val isOtherSpeedableTask: Boolean
        get() {
            return  !thread.isDone()  && !thread.isCancelled()
                    && !downloadThread.stateManager.isSpeedUpRequired
                    && downloadThread.currentTaskState.aliveAndHasNextState()
        }
}