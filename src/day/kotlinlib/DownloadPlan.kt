package day.kotlinlib

import day.kotlinhelper.TimeSpan
import java.util.*

/**
 * Created by a_day_000 on 13.08.2015.
 */
/**
 * Creates tasks and usefull schedule parameters for download
 */
class DownloadPlan(val args: Array<String>) {

    var threadsCount : Int = 0
        private  set
    var pendingTasks = linkedListOf<DownloadTask>()
        private  set
    val argsParser = ArgsParser(args)

    init {


        val fileParser = TaskFileParser(argsParser.taskFilePath ?: throw ExceptionInInitializerError("please provide file with download tasks"),
                argsParser.resultsDir ?: throw ExceptionInInitializerError("please provide output path")
        )
        val tasksCount = fileParser.createdTaskParams.count()
        val argsThreadsCount = argsParser.threadsCount ?: 0
        threadsCount = if (argsThreadsCount < tasksCount)  argsThreadsCount else tasksCount
        val bytesPerSecond : Long = argsParser.downloadSpeed ?: 0L
        fileParser.createdTaskParams.forEach { x ->
            pendingTasks.add(DownloadTask(x.first, x.second, TaskState.NORMAL,SimpleDownloadPortion(bytesPerSecond, TimeSpan.S.millis) ))
        }
        if (threadsCount == 0 || pendingTasks.count() ==0)
            throw ExceptionInInitializerError("Nothing to download")
    }
}
