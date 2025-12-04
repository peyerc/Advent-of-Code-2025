package day4

import java.io.File
import kotlin.time.measureTime

private val map = File("./src/day4/sample_input.txt")
    .readLines()
    .filter { it.isNotBlank() }


fun main() {
    println("Day 3 - Printing Department")

    measureTime {
        val (map, count) = removeRolls(map)

        printMap(map)
        println()

        println("Part I: Total movable rolls are $count")
    }.also {
        println("Execution time: $it")
        println()
    }

    measureTime {
        var map = map
        var currentCount: Int

        do {
            val result = removeRolls(map)
            map = result.first
            currentCount = result.second

            printMap(map)
            println()
        } while (currentCount != 0)

        println("Part II: Total remaining rolls are ${day4.map.countOfRolls - map.countOfRolls}")
    }.also {
        println("Execution time: $it")
    }

}

private val List<String>.countOfRolls: Int
    get() {
        var count = 0
        this.forEach { line ->
            line.forEach { char ->
                if (char == '@') count++
            }
        }
        return count
    }

fun removeRolls(map: List<String>): Pair<List<String>, Int> {
    var count = 0
    val newMap = mutableListOf<String>()
    map.forEachIndexed { y, line ->
        var currentLine = ""
        line.forEachIndexed { x, char ->
            when (char) {
                '@' -> {
                    if (map.checkSurroundings(x, y) < 4) {
                        count++
                        currentLine += 'x'
                    } else {
                        currentLine += char
                    }
                }
                'x' -> {
                    currentLine += '.'
                }
                else -> {
                    currentLine += char
                }
            }
        }
        newMap.add(currentLine)
    }
    return newMap to count
}

private fun printMap(map: List<String>) {
    map.forEachIndexed { y, line ->
        line.forEachIndexed { x, char ->
            when (char) {
                '@' -> {
                    if (map.checkSurroundings(x, y) < 4) {
                        print("x")
                    } else {
                        print(char)
                    }
                }
                else -> print(char)
            }
        }
        println()
    }
}

private fun List<String>.checkSurroundings(x: Int, y: Int): Int {
    val surroundings = listOf(
        Pair(x -1, y -1), // top-left
        Pair(x, y -1),    // top
        Pair(x +1, y -1), // top-right
        Pair(x -1, y),    // left
        Pair(x +1, y),    // right
        Pair(x -1, y +1), // bottom-left
        Pair(x, y +1),    // bottom
        Pair(x +1, y +1)  // bottom-right
    )

    var count = 0
    surroundings.forEach { (x, y) ->
        when (getOrNull(y)?.getOrNull(x)) {
            '@' -> {
                count++
            }
            else -> {
            }
        }
    }

    return count
}
