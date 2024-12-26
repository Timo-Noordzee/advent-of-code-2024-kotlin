package adventofcode

import adventofcode.util.Direction
import adventofcode.util.Point2D
import adventofcode.util.plus
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
class Day15 {

    var input = emptyList<String>()

    @Setup
    fun setup() {
        input = readInput("Day15")
    }

    @Benchmark
    fun part1() = solve()

    @Benchmark
    fun part2(): Int {
        val mapInput = input.takeWhile { it.isNotEmpty() }.map { line ->
            val stringBuilder = StringBuilder()
            line.forEach { tile ->
                stringBuilder.append(
                    when (tile) {
                        '@' -> "@."
                        '#' -> "##"
                        'O' -> "[]"
                        else -> ".."
                    }
                )
            }
            stringBuilder.toString()
        }

        return solve(mapInput)
    }

    private fun solve(mapInput: List<String> = input.takeWhile { it.isNotEmpty() }): Int {
        val m = mapInput.size
        val n = mapInput[0].length
        val map = Array(m) { CharArray(n) { '.' } }

        // Convert the map input to a mutable array of char arrays and find the starting point
        var start = Point2D(0, 0)
        for (i in 0 until m) {
            val line = mapInput[i]
            for (j in 0 until n) {
                map[i][j] = line[j]

                if (line[j] == '@') {
                    start = Point2D(j, i)
                }
            }
        }

        var currentPosition = start
        val moves = input.subList(m + 1, input.size).joinToString("")

        move@ for (move in moves) {
            val direction = when (move) {
                '>' -> Direction.RIGHT
                '<' -> Direction.LEFT
                '^' -> Direction.UP
                'v' -> Direction.DOWN
                else -> error("unknown direction for move $move")
            }

            val pushes = mutableListOf<Pair<Point2D, Point2D>>()
            val seen = mutableSetOf<Point2D>()
            val queue = ArrayDeque<Point2D>()
            queue.add(currentPosition)

            while (queue.isNotEmpty()) {
                val position = queue.removeFirst()
                if (seen.add(position)) {
                    if (direction.isHorizontal) {
                        // Pushing a large box up or down, also check other half
                        when (map[position.y][position.x]) {
                            ']' -> queue.add(position + Direction.LEFT)
                            '[' -> queue.add(position + Direction.RIGHT)
                        }
                    }

                    val nextPosition = position + direction
                    when (map[nextPosition.y][nextPosition.x]) {
                        '#' -> continue@move
                        'O', '[', ']' -> queue.add(nextPosition)
                    }

                    pushes.add(position to nextPosition)
                }
            }

            // Move can be performed, update current position
            currentPosition += direction

            for (i in pushes.lastIndex downTo 0) {
                val (from, to) = pushes[i]
                map[to.y][to.x] = map[from.y][from.x]
                map[from.y][from.x] = '.'
            }
        }

        return map.indices.sumOf { i ->
            val line = map[i]
            line.indices.sumOf { j ->
                val tile = line[j]
                if (tile == '[' || tile == 'O') (100 * i) + j else 0
            }
        }
    }
}

fun main() {
    val day15 = Day15()

    day15.input = readInput("Day15_test")
    check(day15.part1().also { println("test part 1: $it") } == 10092)
    check(day15.part2().also { println("test part 2: $it") } == 9021)

    day15.input = readInput("Day15")
    check(day15.part1().also { println("part 1: $it") } == 1412971)
    check(day15.part2().also { println("part 2: $it") } == 1429299)
}