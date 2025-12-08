package day8

import java.io.File
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.measureTime

private val input = File("./src/day8/sample_input.txt")
    .readLines()
    .map { it.split(",").map { it.toInt() }.let { 
        JunctionBox(it[0], it[1], it[2])
    } }


fun main() {
    println("Day 8: Playground")

    val junctionsBoxes = input.toMutableList()

    val pairs = junctionsBoxes.allPairs().map { (a, b) ->
        a to b to distance(a, b)
    }.sortedBy { it.second }.map { it.first }

    measureTime {
        val circuits = DisjointSetUnion<JunctionBox>()
        val maxSteps = if (input.size > 100) 1000 else 10
        
        pairs
            .take(maxSteps)
            .forEach { connection ->
                val (a, b) = connection
                circuits.union(a, b)
            }
        
        val topThreeCircuits = circuits.sets.map { it.value.size }.sorted().reversed().take(3)
        println("Circuits: $topThreeCircuits")
        println("Part I result: ${topThreeCircuits.reduce { acc, i -> acc * i }}")
    }.also {
        println("Execution time: $it")
    }
    
    measureTime {
        val circuits = DisjointSetUnion<JunctionBox>()
        var lastConnection: Pair<JunctionBox, JunctionBox>? = null

        for (connection in pairs) {
            val (a, b) = connection

            circuits.union(a, b)
            lastConnection = connection

            // Are all junction boxes in a single connected component?
            val allInOneCircuit = circuits.sets.values.any { it.size == junctionsBoxes.size }
            if (allInOneCircuit) break
        }

        lastConnection?.let { (a, b) ->
            println("Last connection made between: $a and $b")
            println("Part II result: ${a.x * b.x}")
        } ?: println("No connections made.")
    }.also {
        println("Execution time: $it")
    }

}

private data class JunctionBox(
    val x: Int,
    val y: Int,
    val z: Int,
) {
    override fun toString(): String = "$x,$y,$z"
}

private fun distance(a: JunctionBox, b: JunctionBox): Double {
    val dx = b.x - a.x
    val dy = b.y - a.y
    val dz = b.z - a.z
    return sqrt( dx.toDouble().pow(2) + dy.toDouble().pow(2) + dz.toDouble().pow(2))
}

private fun <T> List<T>.allPairs(): List<Pair<T, T>> =
    flatMapIndexed { i, a ->
        drop(i + 1).map { b -> a to b }
    }


