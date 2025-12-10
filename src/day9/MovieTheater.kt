package day9

import java.io.File
import javax.imageio.ImageIO
import kotlin.collections.map
import kotlin.math.abs
import kotlin.time.measureTime

private val input = File("./src/day9/sample_input.txt")
    .readLines()
    .map {
        val parts = it.split(",")
        Position(parts[0].toInt(), parts[1].toInt())
    }

fun main() {
    println("Day 9 - Movie Theater")

    measureTime {
        val rectangles = input.allUniquePairs().map {
            it to manhattenRect(it.first, it.second)
        }.sortedBy { it.second }

        println("Part I: Largest rectangle size is ${rectangles.last().second}")
    }.also {
        println("Execution time: $it")
    }

    val polygon = Polygon(input)
    println(polygon)

    val image = polygon.drawToImage()
    ImageIO.write(image, "png", File("day9_interesting_pattern.png"))

    // https://todd.ginsberg.com/post/advent-of-code/2025/day9/
    // https://github.com/tginsberg/advent-2025-kotlin/blob/main/src/main/kotlin/com/ginsberg/advent2025/Day09.kt
    measureTime {
        
        val rects = input.allUniquePairs().map {
            Rect.of(it.first, it.second)
        }.sortedByDescending { it.area }

        val lines: List<Rect> = (input + input.first())
            .zipWithNext()
            .map { (left, right) -> Rect.of(left, right) }

        val result = rects.first { rectangle ->
            val inner = rectangle.inner()
            lines.none { line -> line.overlaps(inner) }
        }.area
        
        println("Part II: $result")
    }.also {
        println("Execution time: $it")
    }

}

private fun manhattenRect(first: Position, second: Position): Long {
    val dx = abs(first.x - second.x) + 1L
    val dy = abs(first.y - second.y) + 1L
    return dx * dy
}

private fun <T> List<T>.allUniquePairs(): List<Pair<T, T>> =
    flatMapIndexed { i, a ->
        drop(i + 1).map { b -> a to b }
    }
