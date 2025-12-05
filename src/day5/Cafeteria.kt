package day5

import java.io.File
import kotlin.time.measureTime

private val input = File("./src/day5/full_input.txt")
    .readText()
    .split("\n\n")
    .let {
        val top = it.first()
        val bottom = it.last()

        val ranges = top.split("\n").map {
            val parts = it.split("-").map { part -> part.trim().toLong() }
            parts[0]..parts[1]
        }
        val ids = bottom.split("\n").map { it.trim().toLong() }

        ranges to ids
    }



fun main() {
    println("Day 5 - Cafeteria")

    val (ranges, ids) = input

    measureTime {
        val part1 = ids.count { id -> ranges.any { range -> id in range } }
        println("Part I: Total valid IDs are $part1")
    }.also {
        println("Execution time: $it")
    }

    measureTime {
        val sortedRanges = ranges.sortedBy { it.first }

        val mergedRanges = mutableListOf<LongRange>()
        var currentMergedRange = sortedRanges.first()
        sortedRanges.windowed(2).map {
            val nextRange = it[1]

            if (currentMergedRange.last < nextRange.first) {
                // No overlap, add the current merged range to the list and start a new one
                mergedRanges.add(currentMergedRange)
                currentMergedRange = nextRange

            } else {
                // Ranges overlap or are contiguous, merge them
                currentMergedRange = currentMergedRange.first..maxOf(currentMergedRange.last, nextRange.last)
            }
        }
        mergedRanges.add(currentMergedRange)

        println("Merged Ranges: ${mergedRanges.joinToString("\n")}")

        val totalIdsInRanges = mergedRanges.sumOf { it.count() }

        println("Part II: Total fresh IDs are $totalIdsInRanges")

    }.also {
        println("Execution time: $it")
    }

}

private fun LongRange.count(): Long =
    (this.last - this.first + 1)
        .coerceAtLeast(0)