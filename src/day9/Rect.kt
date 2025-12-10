package day9

import kotlin.math.max
import kotlin.math.min

// (c) todd.ginsberg
internal class Rect(val x: IntRange, val y: IntRange) {

    val area: Long =
        x.size().toLong() * y.size()

    fun inner(): Rect =
        Rect(
            x.first + 1..<x.last,
            y.first + 1..<y.last
        )

    fun overlaps(other: Rect): Boolean =
        x.overlaps(other.x) && y.overlaps(other.y)

    companion object {
        fun of(a: Position, b: Position): Rect =
            Rect(
                min(a.x, b.x)..max(a.x, b.x),
                min(a.y, b.y)..max(a.y, b.y),
            )
    }
}

private fun IntRange.overlaps(other: IntRange): Boolean =
    max(first, other.first) <= min(last, other.last)

private fun IntRange.size(): Int =
    last - first +1