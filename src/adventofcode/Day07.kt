package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class Day07 {

    var input = emptyList<String>()

    @Setup
    fun setup() {
        input = readInput("Day07")
    }

    @Benchmark
    fun part1() = solve(false)

    @Benchmark
    fun part2() = solve(true)

    private fun solve(concat: Boolean): Long {
        return input.sumOf { line ->
            val (target, numbers) = parseLine(line)
            if (!canSolve(target, numbers, numbers.lastIndex, concat)) {
                return@sumOf 0L
            }
            target
        }
    }

    private fun canSolve(remaining: Long, numbers: List<Long>, index: Int, concat: Boolean): Boolean {
        fun canSolve(remaining: Long) = canSolve(remaining, numbers, index - 1, concat)
        if (index == -1) return remaining == 0L
        if (remaining < 0) return false

        val num = numbers[index]

        if (remaining >= num && canSolve(remaining - num)) {
            return true
        }

        if (remaining % num == 0L && canSolve(remaining / num)) {
            return true
        }

        if (concat && remaining > num) {
            var left = remaining
            var right = num
            while (right != 0L && left % 10 == right % 10) {
                left /= 10
                right /= 10
            }

            if (right == 0L && canSolve(left)) {
                return true
            }
        }

        return false
    }

    private fun parseLine(line: String): Pair<Long, MutableList<Long>> {
        var index = 1
        var target = (line[0] - '0').toLong()
        while (line[index] != ':') {
            target = target * 10 + (line[index++] - '0')
        }

        index++
        index++
        val numbers = mutableListOf<Long>()
        var num = 0L
        while (index < line.length) {
            if (line[index] == ' ') {
                numbers += num
                num = 0
            } else {
                num = num * 10 + (line[index] - '0')
            }
            index++
        }

        numbers += num
        return Pair(target, numbers)
    }
}

fun main() {
    val day07 = Day07()

    day07.input = readInput("Day07_test")
    check(day07.part1() == 3749L)
    check(day07.part2() == 11387L)

    day07.input = readInput("Day07")
    check(day07.part1().also { println(it) } == 1708857123053L)
    check(day07.part2().also { println(it) } == 189207836795655L)
}