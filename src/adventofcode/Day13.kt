package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class Day13 {

    var input = emptyList<String>()

    @Setup
    fun setup() {
        input = readInput("Day13")
    }

    @Benchmark
    fun part1() = solve(0)

    @Benchmark
    fun part2() = solve(10_000_000_000_000L)

    fun solve(offset: Long) = input.chunked(4).sumOf { (buttonA, buttonB, prize) ->
        val ax = buttonA.substringAfter('+').substringBefore(',').toLong()
        val ay = buttonA.substringAfterLast('+').toLong()
        val bx = buttonB.substringAfter('+').substringBefore(',').toLong()
        val by = buttonB.substringAfterLast('+').toLong()
        val px = prize.substringAfter('=').substringBefore(',').toLong() + offset
        val py = prize.substringAfterLast('=').toLong() + offset
        val a = (px * by - py * bx) / (ax * by - ay * bx)
        val b = (ax * py - ay * px) / (ax * by - ay * bx)
        if (a * ax + b * bx == px && a * ay + b * by == py) 3 * a + b else 0L
    }
}

fun main() {
    val day13 = Day13()

    day13.input = readInput("Day13_test")
    check(day13.part1().also { println("test part 1: $it") } == 480L)
    check(day13.part2().also { println("test part 2: $it") } == 875318608908L)

    day13.input = readInput("Day13")
    check(day13.part1().also { println("part 1: $it") } == 39748L)
    check(day13.part2().also { println("part 2: $it") } == 74478585072604L)
}