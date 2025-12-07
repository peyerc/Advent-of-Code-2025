package day6

import java.io.File
import kotlin.time.measureTime

private val input = File("./src/day6/sample_input.txt").readLines()

private val numbers = input.dropLast(1)

private val operations = input.reversed().first().split(Regex("\\s+"))
    .mapNotNull { it.toCharArray().firstOrNull() }


fun main() {
    println("Day 6: Trash Compactor")

    measureTime {
        val results = numbers
            .fold(mutableMapOf<Int,Long>()) { acc, line ->
                line.trim().split(Regex("\\s+")).forEachIndexed { idx, entry ->
                    val currentOperation = operations[idx]
                    acc[idx] = when (currentOperation) {
                        '+' -> (acc[idx] ?: 0) + entry.toLong()
                        '-' -> (acc[idx] ?: 0) - entry.toLong()
                        '*' -> (acc[idx] ?: 1) * entry.toLong()
                        else -> throw IllegalArgumentException("Unknown operation: $currentOperation")
                    }
                }
                acc
            }

        println("Part 1: ${results.map { it.value }.sumOf { it }}")
    }.also {
        println("Execution time: $it")
    }

    measureTime {
        val splitIndices = input.last().mapIndexedNotNull { idx, c ->
            if (c != ' ') idx else null
        }.map { it-1 }.filter { it > 0 }
        val rows = mutableListOf<List<String>>()
        numbers.forEach { line ->
            var lastIndex = 0
            val row = mutableListOf<String>()
            splitIndices.forEach { splitIdx ->
                row.add(line.substring(lastIndex, splitIdx))
                lastIndex = splitIdx + 1
            }
            row.add(line.substring(lastIndex))
            rows.add(row)
        }
        
        val columns = mutableListOf<List<String>>()
        (0 until rows.first().size).forEach { idx ->
            val column = mutableListOf<String>()
            rows.forEach { 
                column.add(it[idx])
            }
            columns.add(column)
        }
        
        val result = columns.mapIndexed { idx, column ->
            calculateColumn(column, operations[idx])
        }.sum()
        
        println("Part 2: $result")
    }.also {
        println("Execution time: $it")
    }
}

private fun calculateColumn(column: List<String>, operation: Char): Long {
    
    val numbers = mutableListOf<Long>()
    (column.first().length-1 downTo 0).forEach { idx ->
        var number = ""
        column.forEach {
            number += it[idx]
        }
        numbers.add(number.trim().toLong())
    }
    
    return when (operation) {
        '+' -> numbers.sum()
        '-' -> numbers.reduce { acc, n -> acc - n }
        '*' -> numbers.reduce { acc, n -> acc * n }
        else -> throw IllegalArgumentException("Unknown operation: $operation")
    }
}