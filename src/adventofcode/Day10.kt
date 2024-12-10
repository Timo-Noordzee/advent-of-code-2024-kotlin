package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class Day10 {

    var input = emptyList<String>()

    @Setup
    fun setup() {
        input = readInput("Day10")
    }

    @Benchmark
    fun part1() = solve(true)

    @Benchmark
    fun part2() = solve(false)

    fun solve(distinct: Boolean): Int {
        val m = input.size
        val n = input[0].length

        val startingPoints = mutableListOf<Pair<Int, Int>>()
        val directions = intArrayOf(0, 1, 0, -1, 0)
        val visited = mutableSetOf<Pair<Int, Int>>()

        // Add a border of '@' to the original input to skip the out-of-bound checks
        val map = Array(m + 2) { CharArray(n + 2) { '@' } }
        for (i in 0 until m) {
            val line = input[i]
            for (j in 0 until n) {
                map[i + 1][j + 1] = line[j]
                if (line[j] == '0') {
                    startingPoints += Pair(i + 1, j + 1)
                }
            }
        }

        var score = 0

        fun hike(i: Int, j: Int, previousHeight: Char) {
            val height = map[i][j]
            if (height - previousHeight != 1) {
                return
            }

            if (height == '9') {
                if (!distinct || visited.add(Pair(i, j))) {
                    score++
                }
                return
            }

            for (d in 0 until 4) {
                hike(i + directions[d], j + directions[d + 1], height)
            }
        }

        startingPoints.forEach { (i, j) ->
            visited.clear()
            // In ASCII '/' has a value one lower than '0'
            hike(i, j, '/')
        }

        return score
    }
}

fun main() {
    val day10 = Day10()

    day10.input = readInput("Day10_test")
    check(day10.part1() == 36)
    check(day10.part2() == 81)

    day10.input = readInput("Day10")
    check(day10.part1().also { println(it) } == 430)
    check(day10.part2().also { println(it) } == 928)
}