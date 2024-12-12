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
class Day12 {

    var input = emptyList<String>()

    @Setup
    fun setup() {
        input = readInput("Day12")
    }

    @Benchmark
    fun part1(): Int {
        val m = input.size
        val n = input[0].length
        val visited = BitSet(m * n)
        val directions = intArrayOf(0, 1, 0, -1, 0)

        var area = 0
        var perimeter = 0

        fun calculate(i: Int, j: Int, region: Char): Boolean {
            if (i !in 0 until m || j !in 0 until n) {
                return true
            }

            if (input[i][j] != region) {
                return true
            }

            val key = i * n + j
            if (visited.get(key)) {
                return false
            }

            visited.set(key)
            area++

            for (d in 0 until 4) {
                if (calculate(i + directions[d], j + directions[d + 1], region)) {
                    perimeter++
                }
            }

            return false
        }

        var totalPrice = 0
        for (i in 0 until m) {
            for (j in 0 until n) {
                val key = i * n + j
                if (!visited.get(key)) {
                    area = 0
                    perimeter = 0
                    calculate(i, j, input[i][j])
                    totalPrice += area * perimeter
                }
            }
        }

        return totalPrice
    }

    @Benchmark
    fun part2() = 0
}

fun main() {
    val day12 = Day12()

    day12.input = readInput("Day12_test")
    check(day12.part1().also { println("test part 1: $it") } == 1930)
//    check(day12.part2().also { println("test part 2: $it") } == 1206)

    day12.input = readInput("Day12")
    check(day12.part1().also { println("part 1: $it") } == 1431316)
//    check(day12.part2().also { println("part 2: $it") } == 0)
}