package day.kotlinhelper

import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.util.logging.Level
import java.util.logging.Logger

/**
 * some helper functions
 */
fun oneIfZero(value : Long) : Long {
    if (value == 0L)
        return 1L
    return value
}

fun oneIfZero(value : Int) : Int {
    if (value == 0)
        return 1
    return value
}

fun printExceptionStackTrace (msg: String, ex : Throwable) : String {
    val sw = StringWriter()
    sw.append(msg)
    val pw =  PrintWriter(sw)
    ex.printStackTrace(pw)
    return sw.toString()
}

fun printExceptionHeader (msg: String?, ex : Throwable) : String {
    val sw = StringWriter()
    if (msg != null) {
        sw.append(msg)
        sw.append(" ")
    }
    sw.append(ex.toString())
    sw.append(" ")
    sw.append(ex.getMessage())

    return sw.toString()
}
fun tryIO(action : () -> Unit,onError : (IOException) -> Unit, finally : (() -> Unit )?) {
    try {
        action()
    } catch(ex : IOException) {
        onError(ex)
    } finally {
        finally?.invoke()
    }
}

fun Logger.logException(ex : Throwable) {
    this.log(Level.SEVERE, printExceptionHeader(null,ex), ex)
}

fun Logger.logHandledException(ex : Throwable) {
    this.log(Level.SEVERE, ex.getMessage())
}