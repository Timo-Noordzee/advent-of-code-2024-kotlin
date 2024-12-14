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
    fun part1() = solve(false) { area, perimeter, _ -> area * perimeter }

    @Benchmark
    fun part2() = solve(true) { area, _, sides -> area * sides }

    private fun solve(detectCorners: Boolean, calculatePrice: (area: Int, perimeter: Int, sides: Int) -> Int): Int {
        val m = input.size
        val n = input[0].length
        val visited = BitSet(m * n)
        val directions = intArrayOf(0, 1, 0, -1, 0)

        // Keep track of the area, perimeter and number of corners of the current region
        var area = 0
        var perimeter = 0
        var corners = 0

        // Returns true if the cell is in the same region
        fun calculate(i: Int, j: Int, region: Char): Boolean {
            if (i !in 0 until m || j !in 0 until n) {
                return false
            }

            if (input[i][j] != region) {
                return false
            }

            val key = i * n + j
            if (visited.get(key)) {
                return true
            }

            visited.set(key)
            area++

            if (detectCorners) {
                val hasUp = i > 0
                val hasDown = i + 1 < m
                val hasLeft = j > 0
                val hasRight = j + 1 < n
                var neighborsMask = 0

                if (hasUp) {
                    if (hasLeft && input[i - 1][j - 1] == region) neighborsMask = neighborsMask or NW
                    if (input[i - 1][j] == region) neighborsMask = neighborsMask or N
                    if (hasRight && input[i - 1][j + 1] == region) neighborsMask = neighborsMask or NE
                }

                if (hasLeft && input[i][j - 1] == region) neighborsMask = neighborsMask or W
                if (hasRight && input[i][j + 1] == region) neighborsMask = neighborsMask or E

                if (hasDown) {
                    if (hasLeft && input[i + 1][j - 1] == region) neighborsMask = neighborsMask or SW
                    if (input[i + 1][j] == region) neighborsMask = neighborsMask or S
                    if (hasRight && input[i + 1][j + 1] == region) neighborsMask = neighborsMask or SE
                }

                val northWestCorner = neighborsMask and (NW or N or W)
                val northEastCorner = neighborsMask and (NE or N or E)
                val southWestCorner = neighborsMask and (SW or S or W)
                val southEastCorner = neighborsMask and (SE or S or E)

                if (northWestCorner == 0 || northWestCorner == (N or W) || northWestCorner == NW) corners++
                if (northEastCorner == 0 || northEastCorner == (N or E) || northEastCorner == NE) corners++
                if (southWestCorner == 0 || southWestCorner == (S or W) || southWestCorner == SW) corners++
                if (southEastCorner == 0 || southEastCorner == (S or E) || southEastCorner == SE) corners++
            }

            for (d in 0 until 4) {
                if (!calculate(i + directions[d], j + directions[d + 1], region)) {
                    perimeter++
                }
            }

            return true
        }

        var totalPrice = 0
        for (i in 0 until m) {
            for (j in 0 until n) {
                val key = i * n + j
                if (!visited.get(key)) {
                    area = 0
                    perimeter = 0
                    corners = 0
                    calculate(i, j, input[i][j])
                    totalPrice += calculatePrice(area, perimeter, corners)
                }
            }
        }

        return totalPrice
    }

    companion object {

        const val N = 1
        const val E = 2
        const val S = 4
        const val W = 8
        const val NW = 16
        const val NE = 32
        const val SW = 64
        const val SE = 128
    }
}

fun main() {
    val day12 = Day12()

    day12.input = readInput("Day12_test")
    check(day12.part1().also { println("test part 1: $it") } == 1930)
    check(day12.part2().also { println("test part 2: $it") } == 1206)

    day12.input = readInput("Day12")
    check(day12.part1().also { println("part 1: $it") } == 1431316)
    check(day12.part2().also { println("part 2: $it") } == 821428)
}