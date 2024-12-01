package adventofcode

import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.benchmark.Warmup
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Scope
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day01 {

    var input = emptyList<String>()

    @Setup
    fun setup() {
        input = readInput("Day01")
    }

    @Benchmark
    fun part1(): Int {
        val left = IntArray(input.size)
        val right = IntArray(input.size)
        var i = 0
        parseInput(input) { a, b ->
            left[i] = a
            right[i++] = b
        }

        left.sort()
        right.sort()

        var totalDistance = 0
        for (i in left.indices) {
            totalDistance += (right[i] - left[i]).absoluteValue
        }
        return totalDistance
    }

    @Benchmark
    fun part2(): Int {
        var i = 0
        val left = IntArray(input.size)
        val right = IntArray(100_000)
        parseInput(input) { a, b ->
            left[i++] = a
            right[b]++
        }

        return left.sumOf { it * right[it] }
    }

    private inline fun parseInput(input: List<String>, onValue: (a: Int, b: Int) -> Unit) {
        input.forEach { line ->
            var i = 0

            var a = line[i++] - '0'
            while (line[i].isDigit()) {
                a = a * 10 + (line[i++] - '0')
            }

            i += 3 // Skip whitespace
            var b = line[i++] - '0'
            while (i < line.length) {
                b = b * 10 + (line[i++] - '0')
            }

            onValue(a, b)
        }
    }
}

fun main() {
    val day01 = Day01()

    day01.input = readInput("Day01_test")
    check(day01.part1() == 11)
    check(day01.part2() == 31)


    day01.input = readInput("Day01")
    check(day01.part1().also { println(it) } == 2176849)
    check(day01.part2().also { println(it) } == 23384288)
}