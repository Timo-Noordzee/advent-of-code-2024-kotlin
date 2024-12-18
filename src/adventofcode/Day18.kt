package adventofcode

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
    fun part2(): Int {
        return 0
    }
}

fun main() {
    val day18 = Day18()

    day18.size = 7
    day18.bytes = 12
    day18.input = readInput("Day18_test")
    check(day18.part1().also { println("test part 1: $it") } == 22)
//    check(day18.part2().also { println("test part 2: $it") } == 0)

    day18.size = 71
    day18.bytes = 1024
    day18.input = readInput("Day18")
    check(day18.part1().also { println("part 1: $it") } == 334)
//    check(day18.part2().also { println("part 2: $it") } == 0)
}