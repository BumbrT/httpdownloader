package  day.kotlinapp
import com.lmax.disruptor.EventFactory
import com.lmax.disruptor.EventHandler
import com.lmax.disruptor.RingBuffer
import com.lmax.disruptor.dsl.Disruptor
import com.lmax.disruptor.EventTranslatorOneArg;
import day.kotlinhelper.logException
import day.kotlinhelper.logHandledException
import day.kotlinlib.DownloadExecutor
import day.kotlinlib.defaultLevel
import day.kotlinlib.traceLogLevel
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.logging.ConsoleHandler
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.util.measureTimeMillis

/**
 * Created by a_day_000 on 02.08.2015.
 */
fun main(args: Array<String>) {
        val consoleHandler = ConsoleHandler()
        consoleHandler.setLevel(defaultLevel)
        val logger = Logger.getAnonymousLogger()
        logger.setUseParentHandlers(false);
        logger.addHandler(consoleHandler)
        logger.setLevel(traceLogLevel)

        //slowdown debug proxy
//        System.setProperty("http.proxyHost", "127.0.0.1");
//        System.setProperty("https.proxyHost", "127.0.0.1");
//        System.setProperty("http.proxyPort", "8888");
//        System.setProperty("https.proxyPort", "8888");

        logger.log(defaultLevel,"starting...")
        try{
                val exec = DownloadExecutor(args,logger)
                exec.start()
        } catch( ex : ExceptionInInitializerError) {
                logger.logHandledException(ex)
        }
    }


