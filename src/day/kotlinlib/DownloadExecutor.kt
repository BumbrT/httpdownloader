package day.kotlinlib

import com.lmax.disruptor.EventFactory
import com.lmax.disruptor.EventHandler
import com.lmax.disruptor.RingBuffer
import com.lmax.disruptor.dsl.Disruptor
import sun.nio.cs.FastCharsetProvider
import java.nio.file.Paths
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter
import kotlin.util.measureTimeMillis

/**
 * Created by a_day_000 on 15.08.2015.
 */
/**
 * Executes all tasks provided by user
 */

public open class DownloadExecutor(val args: Array<String>, val logger : Logger) {
    open val digestIntervalMillis: Long = 500L
    open val waitNextActionIntervalMillis: Long = 10L
    val plan = DownloadPlan(args)
    val scheduler = DownloadScheduler(plan,logger)
    val startedThreads = hashMapOf<Int, ScheduledDownloadTask>()
    val fininshedThreads = hashMapOf<Int, ScheduledDownloadTask>()
    var threadsIdentity: Int = 0
        get() {
            return $threadsIdentity++;
        }
        private set
    val executor = Executors.newCachedThreadPool() //newFixedThreadPool(scheduler.plan.threadsCount + 1)
    private var ringBuffer: RingBuffer<TaskEvent>? = null

    var disruptor : Disruptor<TaskEvent>? = null
    private set
    init {
        if (plan.argsParser.traceFileName != null) {
            val loghandler = FileHandler(Paths.get( plan.argsParser.resultsDir,  plan.argsParser.traceFileName).toString(),false)
            loghandler.setFormatter(SimpleFormatter())
            loghandler.setLevel(traceLogLevel)
            logger.addHandler(loghandler)
        }

        val EVENT_FACTORY = object : EventFactory<TaskEvent> {
            override fun newInstance(): TaskEvent = TaskEvent(null, null)
        }
        val handler = object : EventHandler<TaskEvent> {
            override fun onEvent(p0: TaskEvent?, p1: Long, p2: Boolean) {
                p0?.process();
            }
        }
        disruptor = Disruptor(EVENT_FACTORY, disruptorRingBufferSize  , executor);
        disruptor?.handleEventsWith(handler)
        disruptor?.start()
        ringBuffer = disruptor?.getRingBuffer()
     }

    val eventProducer = ThreadEventProducerWithTranslator(ringBuffer ?: throw ExceptionInInitializerError(" should initialize ring buffer for producer"))

    public fun start() {
        val millis = measureTimeMillis {
            runThreadsFromScheduler()
            continueListening()
        }
        println(scheduler.bytesCounter.toString())
        println("elapsed ${millis/1000.0} seconds")
        executor.shutdown()
        disruptor?.shutdown()


        executor.awaitTermination(2, TimeUnit.SECONDS)
    }

    fun runThreadsFromScheduler() {
        val snapshot = scheduler.DownloadSchedulerStateSnapshot();
        val tasks = snapshot.popNextTasks(startedThreads.count())
        tasks.forEach { task ->
            val id = threadsIdentity
            val thread = DownloadThread(id, task, eventProducer, logger)
            val future = executor.submit(thread)
            logger.log(traceLogLevel,"Submitted thread ${id}")
            val sdt = ScheduledDownloadTask(future, thread)
            startedThreads.put(id, sdt)
            eventProducer.submitCreated(id, task.startState)
        }
    }

    fun removeHaltedThreads() {
        startedThreads.filterTo (fininshedThreads, { entry ->
            val runningTask = entry.getValue()
            if (runningTask.thread.isCancelled() || runningTask.thread.isDone()) {
                eventProducer.submitStateChanged(entry.getKey(), TaskState.DEAD)
                true
            } else
                false
        }
        )
        fininshedThreads.keySet().forEach { key -> startedThreads.remove(key) }
    }

    fun allocateBandwidth() {
        val speedUpPlan = hashMapOf<DownloadThread,TaskState>()
        val snapshot = scheduler.DownloadSchedulerStateSnapshot()
        var freeBandwidth = snapshot.freeBandwidth
        logger.log(traceLogLevel,"allocating free bandwidth ${freeBandwidth}")
        //yes, we can find max allowed TaskState and filter by it..
        val requiredSpeedUpEntries = startedThreads.filter { entry ->
            entry.getValue().isSpeedupRequested
        }.values()
        fun trySpeedUp (threads : Collection<ScheduledDownloadTask>) {
            for (entry in threads) {
                val thread = entry.downloadThread
                val currentState = thread.currentTaskState
                if (currentState.aliveAndHasNextState()) {
                    val nextState =  currentState.nextState()
                    if ((nextState as TaskState).value - currentState.value < freeBandwidth) {
                        logger.log(traceLogLevel,"sppedup succesed")
                        speedUpPlan.put(thread, nextState)
                        freeBandwidth -= nextState.value
                    }
                }
                if (freeBandwidth < TaskState.slowestState.value)
                    break
            }
        }
        trySpeedUp(requiredSpeedUpEntries)
        val sortedAliveThreads = startedThreads.filter { entry ->
            entry.getValue().isOtherSpeedableTask
        }.values().sortDescendingBy { it.downloadThread.currentTaskState}
        trySpeedUp(sortedAliveThreads)
        speedUpPlan.forEach { x -> x.getKey().stateManager.nextState = x.getValue() }
        //while (freeBandwidth > 0)
    }
    fun printExecutorState() {
            val sb = StringBuilder("State report: ")
            startedThreads.values()forEach { x ->
                sb.appendln("Thread ${x.downloadThread.id} state ${x.downloadThread.currentTaskState}") }
            logger.log(traceLogLevel,sb.toString())
    }
    private fun continueListening() {
        Thread.sleep(digestIntervalMillis)
        if(logger.getLevel().intValue() >= traceLogLevel.intValue())// logger.log(traceLogLevel,)
            printExecutorState()
        if (scheduler.hasPendingTasks() || !startedThreads.isEmpty() ) {
            removeHaltedThreads()
            Thread.sleep(waitNextActionIntervalMillis)
            runThreadsFromScheduler()
            Thread.sleep(waitNextActionIntervalMillis)
            allocateBandwidth()
            continueListening()
        }
    }
}