package day.kotlinlib

import day.kotlinhelper.FileSize


/**
 * Created by a_day_000 on 09.08.2015.
 */
/**
 * Parses input args
 */
class ArgsParser(val args: Array<String>) {
    var threadsCount: Int? = null
        private set
    var downloadSpeed: Long? = null
        private set
    var taskFilePath: String? = null
        private set
    var resultsDir: String? = null
        private set
    var traceFileName: String? = null
        private set

    private fun tryParseInt(value: String): Int? {
        try {
            return Integer.parseInt(value);
        } catch (e: NumberFormatException) {
            return null;
        }
    }

    private fun startsWithDelimiter(value: String): Boolean {
        if (value.lastIndex <= 0)
            return false;
        if (value[0] == '-')
            return true;
        return false
    }

    private fun tryExtractTerminalValue(terminal: String, i: Int): String? {
        if (args[i] == terminal) {
            if ( i < args.lastIndex) {
                val nextItem = args[i + 1]
                if ( (nextItem.length() > 0) && !startsWithDelimiter(nextItem))
                    return nextItem
            }
        }
        return null
    }

    private fun tryExtractThreadsCount(i: Int) {
        if (threadsCount != null)
            return
        val terminalValue = tryExtractTerminalValue("-n", i)
        if (terminalValue != null) {
            threadsCount = tryParseInt(terminalValue)
        }
    }

    private fun tryExtractTaskFilePath(i: Int) {
        if (taskFilePath != null)
            return
        taskFilePath = tryExtractTerminalValue("-f", i)
    }

    private fun tryExtractTraceFileName(i: Int) {
        if (traceFileName != null)
            return
        traceFileName = tryExtractTerminalValue("-t", i)
    }

    private fun tryExtractResultsDir(i: Int) {
        if ( resultsDir != null)
            return
        resultsDir = tryExtractTerminalValue("-o", i)
    }

    private fun tryExtractDownloadSpeed(i: Int) {
        if ( downloadSpeed != null)
            return
        val terminalValue = tryExtractTerminalValue("-l", i)
        if (terminalValue != null) {
            val parsedSize = FileSize.tryParse(terminalValue.substring(terminalValue.length() - 1, terminalValue.length()))
            var intLimit: Int?
            if (parsedSize == null) {
                intLimit = tryParseInt(terminalValue.substring(0, terminalValue.length()))
                if (intLimit != null) {
                    downloadSpeed = intLimit.toLong()
                }
            } else {
                intLimit = tryParseInt(terminalValue.substring(0, terminalValue.length() - 1))
                if (intLimit != null) {
                    downloadSpeed = intLimit * parsedSize.bytes
                }
            }
        }
    }


    init {
         if (args.lastIndex <= 0)
            throw  ExceptionInInitializerError("please use arguments")
        for ( i in 0..args.lastIndex-1) {
            if (this.startsWithDelimiter(args[i]) ) {
                this.tryExtractThreadsCount(i)
                this.tryExtractTaskFilePath(i)
                this.tryExtractResultsDir(i)
                this.tryExtractDownloadSpeed(i)
                this.tryExtractTraceFileName(i)
            }
        }

        if (threadsCount == null || downloadSpeed == null || taskFilePath == null || resultsDir == null)
            throw ExceptionInInitializerError("params should contain all necessary parts")
    }

}