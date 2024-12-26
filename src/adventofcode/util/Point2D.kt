package adventofcode.util

data class Point2D(val x: Int, val y: Int)

val Point2D.neighbors
    get() = arrayOf(copy(x = x + 1), copy(x = x - 1), copy(y = y + 1), copy(y = y - 1))

operator fun Point2D.plus(direction: Direction) = copy(
    x = x + direction.horizontalDelta,
    y = y + direction.verticalDelta
)