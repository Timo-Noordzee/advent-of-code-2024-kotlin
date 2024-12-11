package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
class Day11 {

    var input = emptyList<String>()

    @Setup
    fun setup() {
        input = readInput("Day11")
    }

    @Benchmark
    fun part1(): Long {
        return solve(25)
    }

    @Benchmark
    fun part2(): Long {
        return solve(75)
    }

    fun solve(numberOfBlinks: Int): Long {
        val cache = Array(numberOfBlinks + 1) { mutableMapOf<Long, Long>() }

        fun blink(stone: Long, remainingBlinks: Int): Long {
            if (remainingBlinks == 0) {
                return 1L
            }

            fun blink(stone: Long) = blink(stone, remainingBlinks - 1)

            return cache[remainingBlinks].getOrPut(stone) {
                when (stone) {
                    0L -> blink(1L)
                    in 10L..99L -> blink(stone / 10) + blink(stone % 10)
                    in 1000L..9999L -> blink(stone / 100) + blink(stone % 100)
                    in 100000L..999999L -> blink(stone / 1000) + blink(stone % 1000)
                    in 10000000L..99999999L -> blink(stone / 10000) + blink(stone % 10000)
                    in 1000000000L..9999999999L -> blink(stone / 100000) + blink(stone % 100000)
                    in 100000000000L..999999999999L -> blink(stone / 1000000) + blink(stone % 1000000)
                    else -> blink(stone * 2024)
                }
            }
        }

        return input.first()
            .split(' ')
            .map { it.toLong() }
            .sortedDescending()
            .sumOf { stone -> blink(stone, numberOfBlinks) }
    }
}

fun main() {
    val day11 = Day11()

    day11.input = readInput("Day11_test")
    check(day11.part1().also { println("test part 1: $it") } == 55312L)
    check(day11.part2().also { println("test part 2: $it") } == 65601038650482L)

    day11.input = readInput("Day11")
    check(day11.part1().also { println("part 1: $it") } == 207683L)
    check(day11.part2().also { println("part 2: $it") } == 244782991106220L)
}