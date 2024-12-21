package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
class Day20 {

    var input = emptyList<String>()

    @Setup
    fun setup() {
        input = readInput("Day20")
    }

    @Benchmark
    fun part1() = solve(2)

    @Benchmark
    fun part2() = solve(20)

    private fun solve(maxCheatLength: Int): Int {
        val m = input.size
        val n = input[0].length

        var startPoint = Pair(0, 0)
        var endPoint = Pair(0, 0)
        val walls = mutableSetOf<Pair<Int, Int>>()
        for (i in 0 until m) {
            for (j in 0 until n) {
                when {
                    input[i][j] == 'S' -> startPoint = Pair(i, j)
                    input[i][j] == 'E' -> endPoint = Pair(i, j)
                    input[i][j] == '#' -> walls += Pair(i, j)
                }
            }
        }

        val directions = intArrayOf(0, 1, 0, -1, 0)
        val path = buildList {
            var current = startPoint
            add(startPoint)

            while (current != endPoint) {
                for (d in 0 until 4) {
                    val next = Pair(current.first + directions[d], current.second + directions[d + 1])
                    if (next !in walls && next != getOrNull(lastIndex - 1)) {
                        current = next
                        break
                    }
                }

                add(current)
            }
        }

        var count = 0
        path.subList(0, path.size - 100).forEachIndexed { startIndex, start ->
            path.subList(startIndex + 100, path.size).forEachIndexed { offset, end ->
                val endIndex = startIndex + 100 + offset
                val cheatLength = start.distanceTo(end)
                val saving = endIndex - startIndex - cheatLength
                if (cheatLength <= maxCheatLength && saving >= 100) {
                    count++
                }
            }
        }
        return count
    }

    private fun Pair<Int, Int>.distanceTo(other: Pair<Int, Int>): Int {
        return (first - other.first).absoluteValue + (second - other.second).absoluteValue
    }
}

fun main() {
    val day20 = Day20()

    day20.input = readInput("Day20")
    check(day20.part1().also { println("part 1: $it") } == 1507)
    check(day20.part2().also { println("part 2: $it") } == 1037936)
}