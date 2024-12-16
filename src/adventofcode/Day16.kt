package adventofcode

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
        val m = input.size
        val n = input[0].length

        var start = Pair(0, 0)
        var end = Pair(0, 0)

        for (i in 0 until m) {
            for (j in 0 until n) {
                when (input[i][j]) {
                    'S' -> start = Pair(i, j)
                    'E' -> end = Pair(i, j)
                }
            }
        }

        val directions = intArrayOf(0, 1, 0, -1, 0)
        val queue = PriorityQueue<IntArray>(compareBy { it[0] })
        queue.add(intArrayOf(0, 0, start.first, start.second))

        val minCost = Array(m) { IntArray(n) { Int.MAX_VALUE } }
        minCost[start.first][start.second] = 0

        while (queue.isNotEmpty()) {
            val (points, direction, i, j) = queue.remove()

            if (i == end.first && j == end.second) {
                return points
            }

            for (d in 0 until 4) {
                val i2 = i + directions[d]
                val j2 = j + directions[d + 1]

                if (input[i2][j2] == '#') {
                    continue
                }

                val newPoints = if (direction == d) points + 1 else points + 1_001

                if (minCost[i2][j2] > newPoints) {
                    queue.add(intArrayOf(newPoints, d, i2, j2))
                    minCost[i2][j2] = newPoints
                }
            }
        }

        error("no solution found")
    }

    @Benchmark
    fun part2(): Int {
        return 0
    }
}

fun main() {
    val day16 = Day16()

    day16.input = readInput("Day16_test")
    check(day16.part1().also { println("test part 1: $it") } == 7036)
//    check(day16.part2().also { println("test part 2: $it") } == 45)

    day16.input = readInput("Day16")
    check(day16.part1().also { println("part 1: $it") } == 99488)
//    check(day16.part2().also { println("part 2: $it") } == 0)
}