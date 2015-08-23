package day.kotlinlib

import java.io.File
import java.net.URL

/**
 * Created by a_day_000 on 12.08.2015.
 */
/**
 * represents task data, file, url, state and allowed portion
 * used by DownloadThread
 */
data class DownloadTask(val file : File, val url : URL, var startState : TaskState, val downloadPortion : DownloadPortion  )