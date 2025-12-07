package day7

import java.io.File
import kotlin.time.measureTime

private val manifold = File("./src/day7/sample_input.txt").readLines()
private val manifoldMap = manifold
    .mapIndexed { y, line ->
        line.mapIndexed { x, c ->
            Position(x, y) to c
        }
    }
    .flatten()
    .associate { (p, c) -> p to c }


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
        println("Number of possible timelines: ${manifoldMap.findTimelines(manifoldMap.startPosition)}")
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

private fun Map<Position, Char>.findTimelines(currentPosition: Position): Long {
        val next = Position(currentPosition.x, currentPosition.y + 1)
        cache[next]?.let {
            return it
        }

        return when (this[next]) {
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

private val Map<Position, Char>.startPosition: Position
    get() = entries.first { (_, char) -> char == 'S' }.key