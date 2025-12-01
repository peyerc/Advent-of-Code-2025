package day1

const val DIAL_SIZE = 100
val instructions = object {}.javaClass.getResourceAsStream("/day1/sample_input.txt")!!
    .bufferedReader()
    .readLines()
    .filter { it.isNotBlank() }
    .map {
        val direction = it.take(1)
        val steps = it.drop(1).toInt()
        direction to steps
    }


fun main() {
    println("Day 1 - Secret Entrance Dial")

    val staringPosition = 50

    println("Dial start position: $staringPosition")

    val (_, zeroCount) = instructions.fold(staringPosition to 0) { acc, instruction ->
        val (direction, steps) = instruction
        val (currentPos, zeroCounter) = acc

        val necessarySteps = steps % DIAL_SIZE
        val (nextPos, zeroOccurrences) = when (direction) {
            "R" -> (currentPos + necessarySteps) % (DIAL_SIZE) to (currentPos + steps) / DIAL_SIZE
            "L" -> (currentPos - necessarySteps).let { newPos ->
                if (newPos < 0) DIAL_SIZE + newPos else newPos
            } to when {
                currentPos == 0 -> steps / DIAL_SIZE
                steps < currentPos -> 0
                else -> 1 + (steps - currentPos) / DIAL_SIZE
            }
            else -> error("Unknown direction")
        }
        println("Direction: $direction, Steps: ${steps.padded}.\t${currentPos.padded} => ${nextPos.padded} (zero crossings: ${zeroOccurrences})")

        nextPos to zeroCounter + zeroOccurrences
    }

    println(">>> ZERO position total hits: $zeroCount <<<")

}

val Int.padded: String
    get() = this.toString().padStart(3)