package adventofcode.util

data class Point2D(val x: Int, val y: Int)

val Point2D.neighbors
    get() = arrayOf(copy(x = x + 1), copy(x = x - 1), copy(y = y + 1), copy(y = y - 1))