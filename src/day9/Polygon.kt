package day9

import java.awt.BasicStroke
import java.awt.Color
import java.awt.RenderingHints
import java.awt.image.BufferedImage

internal data class Polygon (
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