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
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class Day18 {

    var size = 71
    var bytes = 1024
    var input = emptyList<String>()

    @Setup
    fun setup() {
        size = 71
        bytes = 1024
        input = readInput("Day18")
    }

    @Benchmark
    fun part1(): Int {
        val grid = Array(size) { BooleanArray(size) }

        input.take(bytes).forEach {
            val (j, i) = it.split(',').map(String::toInt)
            grid[i][j] = true
        }

        val directions = intArrayOf(0, 1, 0, -1, 0)
        val queue = ArrayDeque<IntArray>()
        queue.add(intArrayOf(0, 0, 0))

        while (queue.isNotEmpty()) {
            val (i, j, steps) = queue.removeFirst()

            if (i + 1 == size && j + 1 == size) {
                return steps
            }

            for (d in 0 until 4) {
                val i2 = i + directions[d]
                val j2 = j + directions[d + 1]

                if (i2 in 0 until size && j2 in 0 until size && !grid[i2][j2]) {
                    queue.add(intArrayOf(i2, j2, steps + 1))
                    grid[i2][j2] = true
                }
            }
        }

        error("no path found")
    }

    @Benchmark
    fun part2(): String {
        val grid = Array(size) { CharArray(size) { '.' } }
        grid[0][0] = 'O'

        // Start with a memory space on which each byte has fallen (worst case)
        input.forEach {
            val (j, i) = it.split(',').map(String::toInt)
            grid[i][j] = '#'
        }

        val target = Point2D(size - 1, size - 1)
        val queue = ArrayDeque<Point2D>()
        queue.add(Point2D(0, 0))

        var byteIndex = input.size

        while (byteIndex > 0) {
            while (queue.isNotEmpty()) {
                val position = queue.removeFirst()

                if (position == target) {
                    return input[byteIndex]
                }

                for (direction in Direction.ALL) {
                    val next = position + direction
                    if (next.x in 0 until size && next.y in 0 until size && grid[next.y][next.x] == '.') {
                        grid[next.y][next.x] = 'O'
                        queue.add(next)
                    }
                }
            }

            // If no path was found, remove the last byte that has fallen and continue the search
            val (j, i) = input[--byteIndex].split(',').map(String::toInt)
            val bytePosition = Point2D(j, i)

            grid[i][j] = '.'
            for (direction in Direction.ALL) {
                val neighbor = bytePosition + direction
                if (neighbor.x in 0 until size && neighbor.y in 0 until size && grid[neighbor.y][neighbor.x] == 'O') {
                    queue.add(neighbor)
                }
            }
        }

        error("no solution found")
    }
}

fun main() {
    val day18 = Day18()

    day18.size = 7
    day18.bytes = 12
    day18.input = readInput("Day18_test")
    check(day18.part1().also { println("test part 1: $it") } == 22)
    check(day18.part2().also { println("test part 2: $it") } == "6,1")

    day18.size = 71
    day18.bytes = 1024
    day18.input = readInput("Day18")
    check(day18.part1().also { println("part 1: $it") } == 334)
    check(day18.part2().also { println("part 2: $it") } == "20,12")
}