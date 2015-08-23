package day.kotlinlib

import day.kotlinhelper.ByteSizeNames
import day.kotlinhelper.FileSize

/**
 * Created by a_day_000 on 16.08.2015.
 */
/**
 * Counts downloaded bytes
 */
class DownloadedBytesCounter() {
    var totalBytes : Long =0L
        private set

    val archivedBytes = linkedListOf<Long>()
    override fun toString() : String {
        val sb = StringBuilder()
        sb.appendln("Downloaded:")
        sb.appendln(bytesToString(totalBytes))
        archivedBytes.forEach { x ->
            sb.appendln("plus")
            sb.appendln(bytesToString(x))
        }
        return sb.toString()
    }
    private fun bytesToString(bytes : Long) : String {
        if (bytes > FileSize.G.bytes) {
            return "${(bytes / FileSize.G.bytes).toString()} ${ByteSizeNames.G.name} (${bytes} bytes)"
        } else if (bytes > FileSize.M.bytes) {
            return "${(bytes / FileSize.M.bytes).toString()} ${ByteSizeNames.M.name} (${bytes} bytes)"
        } else if (bytes > FileSize.K.bytes) {
            return "${(bytes / FileSize.K.bytes).toString()} ${ByteSizeNames.K.name} (${bytes} bytes)"
        } else {
            return "${bytes.toString()} bytes"
        }
    }
    fun addBytes(bytes: Int) {
        addBytes(bytes.toLong())
    }
    fun addBytes(bytes: Long) {
        val counted = bytes + totalBytes
        if (counted > 0)
            totalBytes = counted
        else {
            archiveBytes(bytes)
        }
    }
    private fun archiveBytes(bytes: Long) {
        archivedBytes.add(totalBytes)
        totalBytes = bytes
    }
}