package day.kotlinlib

import java.io.InvalidObjectException



/**
 * Represents a download task state
 */

enum class TaskState(val value: Int) {
      DEAD(0), FILE_ERROR(1), SLEEP(2), FINISHED(3), ALMOST_DEAD(4), VERY_SLOW(8), SLOW(16), NORMAL(32), FAST(48), TURBO(56), GOD(60);

    companion object StateConverter {
        private val sequenceIndexBuilder = hashMapOf<TaskState, Int>()
        init  {
            TaskState.values().forEachIndexed { i, taskState ->

                sequenceIndexBuilder.put(taskState,i)
            }
        }

        val fastState = arrayOf(FAST,TURBO,GOD)
        val slowState = arrayOf(ALMOST_DEAD,VERY_SLOW,SLOW)
        val deadState = arrayOf(DEAD,FILE_ERROR,SLEEP, FINISHED)

        private val sequenceIndex  : Map<TaskState, Int> = sequenceIndexBuilder

        val notDeadStates = slowState.plus(NORMAL).plus(fastState).toTypedArray()
        val slowestState = ALMOST_DEAD

        val speedableThreads = arrayOf(NORMAL,FAST,TURBO).plus(slowState)
        fun getAllowedStateForNewTask(current : Int) : TaskState?{
            if (current >= TaskState.NORMAL.value)
                return TaskState.NORMAL
            else if (current < TaskState.ALMOST_DEAD.value)
                return null
            else {
                (slowState.lastIndex ..0).forEach { i ->
                    if (current >= slowState[i].value)
                        return slowState[i]
                }
            }
            return null
        }

    }
    fun allowedBytesToDownload( totalBytes: Int) : Int {
        return when (this) {
            TaskState.NORMAL -> totalBytes
            in deadState -> 0
            else -> (totalBytes * this.value.toDouble() / NORMAL.value).toInt()
        }
    }

    fun hasPreviousNotDeadState() : Boolean {
        return this > ALMOST_DEAD
    }
    fun aliveAndHasNextState() : Boolean {
        return this < GOD
    }
    fun previousState() : TaskState? {
        val index = sequenceIndex[this] ?: throw InvalidObjectException("can't find ${this.toString()} in index of enum")
        if (index == 0)
            return null
        else
            return TaskState.values()[index-1]
    }
    fun nextState() : TaskState? {
        val index = sequenceIndex[this] ?: throw InvalidObjectException("can't find ${this.toString()} in index of enum")
        if (index == TaskState.values().lastIndex)
            return null
        else
            return TaskState.values()[index+1]
    }
}





