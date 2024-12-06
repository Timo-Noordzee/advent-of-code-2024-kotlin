package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
class Day06 {

    var input = emptyList<String>()

    @Setup
    fun setup() {
        input = readInput("Day06")
    }

    @Benchmark
    fun part1(): Int {
        val (map, start) = parseMap(input)
        return getPath(map, start).size
    }

    @Benchmark
    fun part2(): Int {
        val (map, start) = parseMap(input)
        return getPath(map, start).count { causesLoop(map, start, it) }
    }

    private fun parseMap(input: List<String>): Pair<Array<CharArray>, Pair<Int, Int>> {
        val m = input.size
        val n = input[0].length
        val map = Array(m) { CharArray(n) { '.' } }

        var start = Pair(0, 0)
        for (i in 0 until m) {
            val line = input[i]
            for (j in 0 until n) {
                when (line[j]) {
                    '#' -> map[i][j] = '#'
                    '^' -> {
                        map[i][j] = 'x'
                        start = Pair(i, j)
                    }
                }
            }
        }

        return Pair(map, start)
    }

    private fun getPath(map: Array<CharArray>, start: Pair<Int, Int>): MutableList<Pair<Int, Int>> {
        var d = 3
        var (i, j) = start
        val m = map.size
        val n = map[0].size

        // Keep track of a list of the path travelled by the guard
        val path = mutableListOf<Pair<Int, Int>>()
        path += Pair(i, j)

        i += directions[d]
        j += directions[d + 1]

        // Keep moving as long as the guard stays within the map
        while (i in 0 until m && j in 0 until n) {
            when (map[i][j]) {
                // The current position is still unvisited
                '.' -> {
                    map[i][j] = 'x'
                    path += Pair(i, j)
                }
                // There is an obstacle at the current position
                '#' -> {
                    // Move 1 back because guard is currently standing inside an obstacle
                    i -= directions[d]
                    j -= directions[d + 1]

                    // Rotate guard to the right
                    d = (d + 1) and 3
                }
            }

            // Move the guard forward
            i += directions[d]
            j += directions[d + 1]
        }

        return path
    }

    private fun causesLoop(map: Array<CharArray>, start: Pair<Int, Int>, obstacle: Pair<Int, Int>): Boolean {
        // Obstacle can't be placed at starting position
        if (start == obstacle) {
            return false
        }

        val m = map.size
        val n = map[0].size
        val seen = mutableSetOf<Triple<Int, Int, Int>>()

        var d = 3
        var (i, j) = start

        i += directions[d]
        j += directions[d + 1]

        // Mark new obstacle on map
        map[obstacle.first][obstacle.second] = '#'

        val causesLoop = kotlin.run loops@{
            while (i in 0 until m && j in 0 until n) {
                if (map[i][j] == '#') {
                    if (!seen.add(Triple(i, j, d))) {
                        return@loops true
                    }

                    // Move 1 back because guard is currently standing inside an obstacle
                    i -= directions[d]
                    j -= directions[d + 1]

                    // Rotate guard to the right
                    d = (d + 1) and 3
                }

                i += directions[d]
                j += directions[d + 1]
            }

            false
        }

        // Remove new obstacle from map
        map[obstacle.first][obstacle.second] = 'x'
        return causesLoop
    }

    companion object {

        private val directions = intArrayOf(0, 1, 0, -1, 0)
    }
}

fun main() {
    val day06 = Day06()

    day06.input = readInput("Day06_test")
    check(day06.part1() == 41)
    check(day06.part2() == 6)

    day06.input = readInput("Day06")
    check(day06.part1().also { println(it) } == 5080)
    check(day06.part2().also { println(it) } == 1919)
}