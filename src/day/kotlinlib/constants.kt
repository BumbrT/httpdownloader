package day.kotlinlib

import java.lang
import java.util.logging.Level
/**
 * Created by a_day_000 on 03.08.2015.
 */
/**
 * Constants referenced by app
 */
val x2BufferSizeRezervation = 2; //download buffer reservation for speed increase

val traceLogLevel = Level.FINE; // log level used for trace output
val defaultLevel = Level.INFO; // log level used for normal output

val disruptorRingBufferSize = 1024 // disruptor default ring buffer size, actually a magic number
