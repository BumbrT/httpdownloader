package day.kotlinlib

import day.kotlinhelper.logException
import day.kotlinhelper.tryIO
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URI
import java.net.URL
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.util.measureTimeMillis

/**
 * Created by a_day_000 on 05.08.2015.
 */
/**
 * represents state of running file download thread, uses TaskStateMatager for manage internal state, sends messages via producer
 */
open class DownloadThread(val id: Int, val downloadTask: DownloadTask, val producer: ThreadEventProducerWithTranslator, val logger : Logger) : Runnable {
    init {
        if (downloadTask.startState in TaskState.deadState) {
            throw ExceptionInInitializerError("should pass alive state ${downloadTask.startState.toString()}")
        }
    }
    private var allowedBytes : Int = downloadTask.startState.allowedBytesToDownload(downloadTask.downloadPortion.bytesPerPortion)
    var currentTaskState: TaskState = downloadTask.startState
        private set(value) {
            $currentTaskState = value
            allowedBytes = value.allowedBytesToDownload(downloadTask.downloadPortion.bytesPerPortion)
            producer.submitStateChanged(id, value)
        }

    /**
     * manages internal class state
     */
    open inner class TaskStateMatager {
        public  var slowdownAttempts: Int = 0
        private set
        public  var speedUpAttempts: Int = 0
        private set
        private open val slowAfterAttempts: Int = 10
        private open val speedUpAfterAttempts: Int = 10
        private var nextSlowDownInternalState: TaskState? = null
        public var nextState : TaskState? = null
        public var isSpeedUpRequired: Boolean = false
        private set
        fun trySlowDown() {
            speedUpAttempts = 0
            if (currentTaskState.hasPreviousNotDeadState()) {
                if (++slowdownAttempts >= slowAfterAttempts) {
                    logger.log(traceLogLevel, "thread ${id} reported slowdown, attempts ${slowdownAttempts}")
                    nextSlowDownInternalState = currentTaskState.previousState() as TaskState
                    slowdownAttempts = 0
                }
            }
        }
        fun trySpeedUp() {
            slowdownAttempts = 0
            if (currentTaskState.aliveAndHasNextState() && !isSpeedUpRequired) {
                if (++speedUpAttempts >= speedUpAfterAttempts) {
                    isSpeedUpRequired = true
                    speedUpAttempts = 0
                }
            }
        }
        fun digest() {
            if (nextState != null) {
                currentTaskState = nextState as TaskState
                isSpeedUpRequired = false
                nextState = null
                return
            }
            if (nextSlowDownInternalState != null) {
                currentTaskState = nextSlowDownInternalState as TaskState
                isSpeedUpRequired = false
                nextSlowDownInternalState = null
            }

        }
    }

    val stateManager: TaskStateMatager = TaskStateMatager()
    override fun run() {
        logger.log(traceLogLevel,"Thread ${id} started")
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        tryIO ({
            inputStream = downloadTask.url.openStream();
            outputStream = FileOutputStream(downloadTask.file);

            var readedBytes: Int = 0;
            val bytes: ByteArray = ByteArray(downloadTask.downloadPortion.bytesPerPortion * x2BufferSizeRezervation);
            do {

                if (allowedBytes == 0) {
                    readedBytes= -1;
                    //stateManager.goSleep()
                    //Thread.sleep(downloadTask.downloadPortion.millisecondsPerPortion)
                } else {
                    val elapsedMilis = measureTimeMillis {
                        readedBytes = inputStream!!.read(bytes, 0, allowedBytes)
                        if (readedBytes != -1) {
                            outputStream?.write(bytes, 0, readedBytes);
                        }
                    }
                    if (readedBytes != -1) {
                        producer.submitDownloadedBytes(readedBytes)
                        val sleepTime = downloadTask.downloadPortion.getThrottleSleepInterval(readedBytes, elapsedMilis)
                        if ( sleepTime == 0L) {
                            stateManager.trySlowDown()
                        } else {
                            stateManager.trySpeedUp()
                            Thread.sleep(sleepTime)
                        }
                        stateManager.digest()
                    }
                }

            } while (readedBytes != -1);

            logger.log(traceLogLevel,"Thread ${id} is done")

        },  { ex ->
            currentTaskState = TaskState.DEAD
            logger.logException(ex)
        } , {
            tryIO({outputStream?.close()}, {ex ->logger.logException(ex)}, null)
            tryIO({inputStream?.close()}, {ex ->logger.logException(ex)}, null)

        })
    }
}