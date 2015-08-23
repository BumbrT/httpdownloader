package day.kotlinhelper

import java.util.concurrent.TimeUnit


// simplified TimeUnit
enum class TimeSpan(val millis: Long) {
    Millisecond(1L),
    S(1000L),
    M(6000L),
    H(360000L);
    companion object TimeSpanConverter {
        fun tryParse(value : String) : TimeSpan? {
              try {
                return TimeSpan.valueOf(value.toUpperCase())
            } catch(ex: IllegalArgumentException) {
                return null;
            }
        }
    }
}