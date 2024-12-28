package adventofcode.util

@JvmInline
value class Direction(val value: Int) {

    val horizontalDelta
        get() = if (value and 1 == 1) 2 - value else 0

    val verticalDelta
        get() = if (value and 1 == 0) value - 1 else 0

    val opposite
        get() = Direction(value xor 2)

    val isHorizontal
        get() = value and 1 == 0

    val rotateClockwise
        get() = when (this) {
            UP -> RIGHT
            RIGHT -> DOWN
            DOWN -> LEFT
            LEFT -> UP
            else -> error("unknown direction")
        }

    val rotateCounterclockwise
        get() = when (this) {
            UP -> LEFT
            LEFT -> DOWN
            DOWN -> RIGHT
            RIGHT -> UP
            else -> error("unknown direction")
        }

    companion object {
        val UP = Direction(0)
        val RIGHT = Direction(1)
        val DOWN = Direction(2)
        val LEFT = Direction(3)

        val ALL = arrayOf(UP, RIGHT, DOWN, LEFT)
    }
}