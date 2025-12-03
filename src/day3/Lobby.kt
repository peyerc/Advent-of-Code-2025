package day3

import java.io.File
import kotlin.text.toLong
import kotlin.time.measureTime

private val banks = File("./src/day3/sample_input.txt")
    .readText()
    .split("\n")
    .filter { it.isNotBlank() }


fun main() {
    println("Day 3 - Lobby")

    measureTime {
        val sum = banks.sumOf { it.maxJoltage(2) }
        println("Part I: Total Joltage is $sum")
    }.also {
        println("Execution time: $it")
    }

    measureTime {
        val sum = banks.sumOf { it.maxJoltage(12) }
        println("Part II: Total Joltage is $sum")
    }.also {
        println("Execution time: $it")
    }
}

private fun String.maxJoltage(digits: Int): Long {
    val stack = ArrayDeque<Char>()
    var toRemove = this.length - digits

    for (c in this) {
        while (toRemove > 0 && stack.isNotEmpty() && stack.last() < c) {
            stack.removeLast()
            toRemove--
        }
        stack.addLast(c)
    }

    while (stack.size > digits) {
        stack.removeLast()
    }

    return stack.joinToString("").toLong()
}
