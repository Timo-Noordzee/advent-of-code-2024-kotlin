package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
class Day14 {

    var m = 7
    var n = 11
    var input = emptyList<String>()

    @Setup
    fun setup() {
        m = 103
        n = 101
        input = readInput("Day14")
    }

    @Benchmark
    fun part1(): Int {
        var topLeft = 0
        var topRight = 0
        var bottomLeft = 0
        var bottomRight = 0

        val midX = n / 2
        val midY = m / 2

        input.forEach { line ->
            val (x, y, vx, vy) = line.split(' ').flatMap {
                it.substringAfter('=').split(',').map(String::toInt)
            }

            val i = (y + 100 * vy).mod(m)
            val j = (x + 100 * vx).mod(n)

            when {
                i in 0 until midY && j in 0 until midX -> topLeft++
                i in 0 until midY && j in (midX + 1) until n -> topRight++
                i in (midY + 1) until m && j in 0 until midX -> bottomLeft++
                i in (midY + 1) until m && j in (midX + 1) until n -> bottomRight++
            }
        }

        return topLeft * topRight * bottomLeft * bottomRight
    }

    @Benchmark
    fun part2(): Int {
        val robots = input.map { line ->
            line.split(' ').flatMap { it.substringAfter('=').split(',').map(String::toInt) }
        }

        val grid = Array(m) { IntArray(n) { -1 } }

        repeat(100_000) { t ->
            robots.forEach { (x, y, vx, vy) ->
                val i = (y + t * vy).mod(m)
                val j = (x + t * vx).mod(n)
                grid[i][j] = t
            }

            // Find the maximum number of robots consecutively standing in a row
            var maxLength = 0
            grid.forEach { line ->
                var length = 0
                for (i in line.indices) {
                    if (line[i] == t) {
                        length++
                    } else {
                        maxLength = length.coerceAtLeast(maxLength)
                        length = 0
                    }
                }
                maxLength = length.coerceAtLeast(maxLength)
            }

            // Verified using println that the Christmas tree contains at least 20 robots in a row
            if (maxLength >= 20) {
                return t
            }
        }

        error("No Christmas tree found!")
    }
}

fun main() {
    val day14 = Day14()

    day14.m = 7
    day14.n = 11
    day14.input = readInput("Day14_test")
    check(day14.part1().also { println("test part 1: $it") } == 12)

    day14.m = 103
    day14.n = 101
    day14.input = readInput("Day14")
    check(day14.part1().also { println("part 1: $it") } == 225552000)
    check(day14.part2().also { println("part 2: $it") } == 7371)
}