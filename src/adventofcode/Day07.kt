package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

private typealias Operation = (value: Long, num: Int) -> Long

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
class Day07 {

    var input = emptyList<String>()

    @Setup
    fun setup() {
        input = readInput("Day07")
    }

    @Benchmark
    fun part1(): Long {
        val operations = listOf<Operation>(Long::plus, Long::times)
        return solve(operations)
    }

    @Benchmark
    fun part2(): Long {
        val operations = listOf<Operation>(
            Long::plus,
            Long::times,
            { a, b -> a concat b }
        )
        return solve(operations)
    }

    private fun solve(operations: List<Operation>): Long {
        return input.sumOf { line ->
            val (target, numbers) = parseLine(line)
            if (!canSolve(target, numbers, operations, 1, numbers[0].toLong())) {
                return@sumOf 0L
            }
            target
        }
    }

    private fun canSolve(
        target: Long,
        numbers: List<Int>,
        operations: List<Operation>,
        index: Int,
        value: Long
    ): Boolean {
        if (index == numbers.size) return target == value
        if (value > target) return false

        val num = numbers[index]
        return operations.any { operation ->
            canSolve(target, numbers, operations, index + 1, operation.invoke(value, num))
        }
    }

    private fun parseLine(line: String): Pair<Long, MutableList<Int>> {
        var index = 1
        var target = (line[0] - '0').toLong()
        while (line[index] != ':') {
            target = target * 10 + (line[index++] - '0')
        }

        index++
        index++
        val numbers = mutableListOf<Int>()
        var num = 0
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

    private infix fun Long.concat(other: Int): Long {
        return when {
            other < 10 -> this * 10 + other
            other < 100 -> this * 100 + other
            else -> this * 1000 + other
        }
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