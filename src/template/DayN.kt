package template

import java.io.File
import kotlin.time.measureTime

private val input = File("./src/dayN/sample_input.txt")
    .readText()


fun main() {
    println("Day N - ???")

    measureTime {
        println("Part I: ???")
    }.also {
        println("Execution time: $it")
    }

    measureTime {
        println("Part II: ???")
    }.also {
        println("Execution time: $it")
    }
}