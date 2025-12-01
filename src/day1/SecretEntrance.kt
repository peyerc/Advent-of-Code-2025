package day1

import java.io.File

private const val DIAL_SIZE = 100
private val instructions = File("./src/day1/sample_input.txt")
    .readLines()
    .filter { it.isNotBlank() }
    .map {
        val direction = it.take(1)
        val steps = it.drop(1).toInt()
        direction to steps
    }


fun main() {
    println("Day 1 - Secret Entrance Dial")

    val result = instructions.fold(EntranceDialAccumulator()) { acc, instruction ->
        val (direction, steps) = instruction

        val necessarySteps = steps % DIAL_SIZE
        val (nextPos, zeroOccurrences) = when (direction) {
            "R" -> (acc.currentPosition + necessarySteps) % (DIAL_SIZE) to (acc.currentPosition + steps) / DIAL_SIZE
            "L" -> (acc.currentPosition - necessarySteps).let { newPos ->
                if (newPos < 0) DIAL_SIZE + newPos else newPos
            } to when {
                acc.currentPosition == 0 -> steps / DIAL_SIZE
                steps < acc.currentPosition -> 0
                else -> 1 + (steps - acc.currentPosition) / DIAL_SIZE
            }
            else -> error("Unknown direction")
        }
        val posIsZero = nextPos == 0
        println("Direction: $direction, Steps: ${steps.padded}.\t${acc.currentPosition.padded} => ${nextPos.padded} (zero reached: $posIsZero, zero crossings: $zeroOccurrences)")
        EntranceDialAccumulator(
            currentPosition = nextPos,
            zeroHits = acc.zeroHits + if (posIsZero) 1 else 0,
            zeroVisits = acc.zeroVisits + zeroOccurrences
        )
    }

    println("Part I: Zero hits: ${result.zeroHits}")
    println("Part II: Zero visits: ${result.zeroVisits}")
}

private val Int.padded: String
    get() = this.toString().padStart(3)

private data class EntranceDialAccumulator(
    val currentPosition: Int = 50,
    val zeroHits: Int = 0,
    val zeroVisits: Int = 0,
)
