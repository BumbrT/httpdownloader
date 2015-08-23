import day.kotlinhelper.*
import day.kotlinlib.*
import org.junit.Test
import java.util.regex.Pattern
import kotlin.reflect.jvm.java
import kotlin.test.assertTrue
import kotlin.test.failsWith

/**
 * Created by a_day_000 on 10.08.2015.
 */
public class ArgsParserTest {
    fun shouldBeParsedCorrectly(s : String) {
        val  args = s.split(Pattern.compile("[ ]+"))
        ArgsParser(args.toTypedArray() )
    }
    fun shouldFailInInitialization ( s : String) {
        failsWith(ExceptionInInitializerError::class.java, { shouldBeParsedCorrectly(s) })
    }
    Test fun runTest(): Unit {
        shouldBeParsedCorrectly("-n 1 -l 2 -f gg.txt -o output_dir")
        shouldBeParsedCorrectly("-n 1 -l 200000 -f gg.txt -o output_dir")
        shouldBeParsedCorrectly("-n 1 -l 40000 -f gg.txt -o output_dir")
        shouldBeParsedCorrectly("asdf sadf -n 15 -l 20m -f gg.txt -o output_dir")
        shouldBeParsedCorrectly("-n 100 -l 2g asdf asdf -f gg.txt -o output_dir")
        shouldBeParsedCorrectly("-n 100 -l 2g asdf asdf -f gg.txt -o output_dir")

        shouldFailInInitialization("-n 100 -l 2u asdf asdf -f gg.txt -o output_dir")
        shouldFailInInitialization(" -l 2k -f gg.txt -o output_dir")
        shouldFailInInitialization("-n 1 -f gg.txt -o output_dir")
        shouldFailInInitialization("-n 1 -l 2k -o output_dir")
        shouldFailInInitialization("-n 1 -l 2k -f gg.txt ")
        shouldFailInInitialization("-n  -l 2k -f gg.txt -o output_dir")
        shouldFailInInitialization("-n 1 -l  -f gg.txt -o output_dir")
        shouldFailInInitialization("-n 1 -l 2k -f  -o output_dir")
        shouldFailInInitialization("-n 1 -l 2k -f gg.txt -o ")
        shouldFailInInitialization("-n  -l 2k -f gg.txt -o output_dir")
        shouldFailInInitialization("-n 1 -l  -f gg.txt -o output_dir")
        shouldFailInInitialization("-n 1 -l 2k asdfasdf asdf -f  -o output_dir")
        shouldFailInInitialization("-n 1 -l 2k -f gg.txt asdfas asdf  -o ")
        shouldFailInInitialization("-n 1 -l 2000000000000000000 -f gg.txt -o output_dir")
    }
}