package day7

import java.io.File
import kotlin.time.measureTime

private val manifold = File("./src/day7/full_input.txt").readLines()

fun main() {
    println("Day 7: Laboratories")
    
    println("Part 1")
    measureTime {
        var currentFrame = manifold
        currentFrame.print()
        
        while(true) {
            val (nextFrame, splits) = currentFrame.processManifold()
            nextFrame.print()
            println("Total beam splits: $splits")
            if (nextFrame == currentFrame) break
            currentFrame = nextFrame
        }
    }.also {
        println("Execution time: $it")
    }
    
    println("Part 2")
    measureTime { 
        println("Number of possible timelines: ${manifold.findTimelines(manifold.startPosition)}")
    }.also { 
        println("Execution time: $it")
    }
}

private fun List<String>.print() {
    println(joinToString("\n"))
    println()
}

private fun List<String>.processManifold(): Pair<List<String>, Int> {
    val nextManifold = mutableListOf<String>()
    var hits = 0
    this.forEachIndexed { y, line ->
        var currentLine = ""
        line.forEachIndexed { x, c ->
            val aboveLine = this.getOrNull(y-1)
            when {
                aboveLine?.getOrNull(x) == '|' && c == '^' -> {
                    currentLine += '@'
                    hits++
                }
                aboveLine?.getOrNull(x) == 'S' || aboveLine?.getOrNull(x) == '|' -> currentLine += '|'
                else -> currentLine += c
            }
        }
        nextManifold.add(currentLine.processBeamSplit())
    }
    return nextManifold to hits
}

private fun String.processBeamSplit(): String {
    var output = this
    do {
        val foundAt = output.indexOf('@')
        if (foundAt >= 0) {
            output = output.substring(0, foundAt - 1) + "|^|" + output.substring(foundAt + 2)
        }
    } while (output.indexOf('@') != -1)
    return output
}

private val cache = HashMap<Position, Long>()

// Solution from https://github.com/ivzb/playground/blob/master/src/main/kotlin/advent_of_code/_2025/Task07.kt
private fun List<String>.findTimelines(currentPosition: Position): Long {
        val next = Position(currentPosition.x, currentPosition.y + 1)
        cache[next]?.let { return it }

        return when (runCatching { this[next.y][next.x] }.getOrDefault('@')) {
            '.' -> {
                val timelines = findTimelines(next)
                cache[next] = timelines
                timelines
            }
            '^' -> {
                val left = findTimelines(Position(next.x-1, next.y))
                val right = findTimelines(Position(next.x+1, next.y))
                left + right
            }
            else -> 1
        }
    }

data class Position(
    val x: Int,
    val y: Int,
)

private val List<String>.startPosition: Position
    get() {
        this.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                if (c == 'S') return Position(x,y)
            }
        }
        throw IllegalStateException("No start position found")
    }