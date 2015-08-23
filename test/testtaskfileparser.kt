import day.kotlinhelper.*
import day.kotlinlib.*
import org.junit.Test
import java.util.regex.Pattern

/**
 * Created by a_day_000 on 11.08.2015.
 */
public class TestTaskFileParser {
    Test fun shouldCorrectlyParse(): Unit {
        val pars = TaskFileParser("test\\res\\readertestCorrectLines.txt", "out")

        println(pars.failedLines)
        println(pars.parsedValues)
        pars.createdTaskParams.forEach { println(it) }
    }

    Test fun shouldCorrectlyDetectDuplicates(): Unit {
        val pars = TaskFileParser("test\\res\\readertestDuplicate.txt", "out")

        println(pars.failedLines)
        println(pars.parsedValues)
     }

    Test fun shouldCorrectlyDetectInvalidInputs(): Unit {
        val pars = TaskFileParser("test\\res\\readertestInvalidInput.txt", "out")

        println(pars.failedLines)
        println(pars.parsedValues)
     }
}