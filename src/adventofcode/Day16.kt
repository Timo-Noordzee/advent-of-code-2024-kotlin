package adventofcode

import adventofcode.util.Direction
import adventofcode.util.Point2D
import adventofcode.util.plus
import org.openjdk.jmh.annotations.*
import java.util.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
class Day16 {

    var input = emptyList<String>()

    @Setup
    fun setup() {
        input = readInput("Day16")
    }

    @Benchmark
    fun part1(): Int {
        val (size, start, end) = findStandAndEnd()
        val (m, n) = size

        val queue = PriorityQueue<IntArray>(compareBy { it[0] })
        queue.add(intArrayOf(0, Direction.RIGHT.value, start.x, start.y))

        val minCost = Array(m) { IntArray(n) { Int.MAX_VALUE } }
        minCost[start.y][start.x] = 0

        while (queue.isNotEmpty()) {
            val (cost, direction, x, y) = queue.remove()

            if (x == end.x && y == end.y) {
                return cost
            }

            for (nextDirection in allowedDirections[direction]) {
                val x2 = x + nextDirection.horizontalDelta
                val y2 = y + nextDirection.verticalDelta

                if (input[y2][x2] != '#') {
                    val newPoints = if (nextDirection.value == direction) cost + 1 else cost + 1_001

                    if (minCost[y2][x2] > newPoints) {
                        queue.add(intArrayOf(newPoints, nextDirection.value, x2, y2))
                        minCost[y2][x2] = newPoints
                    }
                }
            }
        }

        error("no solution found")
    }

    @Benchmark
    fun part2(): Int {
        val (_, start, end) = findStandAndEnd()

        // The key is an 18-bit mask representing the current state
        // 2 bits (MSB) are used for the direction
        // 8 bits are used for the y coordinate
        // 8 bits (LSB) are used for the x coordinate
        val bestCost = IntArray(0x3FFFF) { Int.MAX_VALUE }
        val links = mutableMapOf<Int, MutableList<Int>>()
        val queue = PriorityQueue<IntArray>(compareBy { it[0] })

        val targetState = (end.y shl 8) or end.x
        val initialState = (Direction.RIGHT.value shl 16) or (start.y shl 8) or start.x
        queue.add(intArrayOf(0, initialState, initialState))

        while (queue.isNotEmpty()) {
            val (cost, state, previousState) = queue.remove()
            if ((state and 0xFFFF) == targetState) {
                links.getOrPut(state) { mutableListOf() }.add(previousState)
                break
            }

            if (bestCost[state] != Int.MAX_VALUE) {
                if (cost == bestCost[state]) {
                    links.getOrPut(state) { mutableListOf() }.add(previousState)
                }

                continue
            }

            bestCost[state] = cost
            links.getOrPut(state) { mutableListOf() }.add(previousState)

            val direction = Direction(state shr 16)
            val position = Point2D(state and 0xFF, (state shr 8) and 0xFF)

            // Check moving forward and first rotating clockwise or counterclockwise and then moving forward
            for (nextDirection in allowedDirections[direction.value]) {
                val nextPosition = position + nextDirection
                if (input[nextPosition.y][nextPosition.x] != '#') {
                    val newState = (nextDirection.value shl 16) or (nextPosition.y shl 8) or nextPosition.x
                    if (direction == nextDirection) {
                        queue.add(intArrayOf(cost + 1, newState, state))
                    } else {
                        queue.add(intArrayOf(cost + 1001, newState, state))
                    }
                }
            }
        }

        val seen = BitSet(0x3FFFF)
        val tiles = BitSet(0xFFFF)
        fun walk(state: Int) {
            if (!seen.get(state)) {
                seen.set(state)
                tiles.set(state and 0xFFFF)
                links[state]?.forEach(::walk)
            }
        }

        for (direction in Direction.ALL) {
            val state = (direction.value shl 16) or (end.y shl 8) or end.x
            walk(state)
        }

        return tiles.cardinality()
    }

    private fun findStandAndEnd(): Triple<IntArray, Point2D, Point2D> {
        val m = input.size
        val n = input[0].length

        var start = Point2D(0, 0)
        var end = Point2D(0, 0)

        for (i in 0 until m) {
            for (j in 0 until n) {
                when (input[i][j]) {
                    'S' -> start = Point2D(j, i)
                    'E' -> end = Point2D(j, i)
                }
            }
        }

        return Triple(intArrayOf(m, n), start, end)
    }

    companion object {
        val allowedDirections = Direction.ALL.map { arrayOf(it, it.rotateClockwise, it.rotateCounterclockwise) }
    }
}

fun main() {
    val day16 = Day16()

    day16.input = readInput("Day16_test")
    check(day16.part1().also { println("test part 1: $it") } == 7036)
    check(day16.part2().also { println("test part 2: $it") } == 45)

    day16.input = readInput("Day16")
    check(day16.part1().also { println("part 1: $it") } == 99488)
    check(day16.part2().also { println("part 2: $it") } == 516)
}