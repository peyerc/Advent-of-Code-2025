package day9

import java.awt.BasicStroke
import java.awt.Color
import java.awt.RenderingHints
import java.awt.image.BufferedImage
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

private data class Polygon (
    val vertices: List<Position>
) {
    init {
        require(vertices.size >= 3) { "Polygon needs at least 3 vertices" }
    }

    /**
     * Draw this polygon into a BufferedImage.
     */
    fun drawToImage(
        imageWidth: Int = 500,
        imageHeight: Int = 500,
        padding: Int = 20,
        backgroundColor: Color = Color.WHITE,
        fillColor: Color = Color(0x33, 0x99, 0xFF, 180), // semi-transparent
        strokeColor: Color = Color.BLACK,
        strokeWidth: Float = 2f
    ): BufferedImage {
        // 1) Create image + Graphics2D
        val img = BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB)
        val g2 = img.createGraphics()

        // Antialiasing for nicer shapes
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        )

        // 2) Background
        g2.color = backgroundColor
        g2.fillRect(0, 0, imageWidth, imageHeight)

        // 3) Compute bounds of polygon coordinates
        val minX = vertices.minOf { it.x }
        val maxX = vertices.maxOf { it.x }
        val minY = vertices.minOf { it.y }
        val maxY = vertices.maxOf { it.y }

        val polyWidth = maxX - minX.toDouble()
        val polyHeight = maxY - minX.toDouble()

        // Avoid divide by zero if all points are same (degenerate)
        val safeWidth = if (polyWidth == 0.0) 1.0 else polyWidth
        val safeHeight = if (polyHeight == 0.0) 1.0 else polyHeight

        // 4) Compute scale so polygon fits into image (with padding)
        val availableWidth = imageWidth - 2 * padding
        val availableHeight = imageHeight - 2 * padding
        val scale = minOf(
            availableWidth / safeWidth,
            availableHeight / safeHeight
        )

        // 5) Map polygon coordinates to pixel coordinates
        val xs = IntArray(vertices.size)
        val ys = IntArray(vertices.size)

        vertices.forEachIndexed { i, p ->
            val sx = ((p.x - minX) * scale + padding).toInt()

            // Invert Y for image coordinates (0 at top)
            val sy = (imageHeight - padding - (p.y - minY) * scale).toInt()

            xs[i] = sx
            ys[i] = sy
        }

        // 6) Draw filled polygon
        g2.color = fillColor
        g2.fillPolygon(xs, ys, vertices.size)

        // 7) Draw stroke
        g2.color = strokeColor
        g2.stroke = BasicStroke(strokeWidth)
        g2.drawPolygon(xs, ys, vertices.size)

        g2.dispose()
        return img
    }
}