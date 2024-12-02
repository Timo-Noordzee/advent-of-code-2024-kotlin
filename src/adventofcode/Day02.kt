package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlin.math.sign

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day02 {

    var input = emptyList<String>()

    @Setup
    fun setup() {
        input = readInput("Day02")
    }

    @Benchmark
    fun part1(): Int {
        return input.count { line ->
            val levels = parseLine(line)
            isSafe(levels, 0)
        }
    }

    @Benchmark
    fun part2(): Int {
        return input.count { line ->
            val levels = parseLine(line)
            isSafe(levels, 1) || isSafe(levels.reversed(), 1)
        }
    }

    // More efficient way of converting line to list of numbers
    private fun parseLine(line: String): List<Int> {
        val levels = mutableListOf<Int>()
        var level = 0
        var i = 0
        while (i < line.length) {
            if (line[i] == ' ') {
                i++
                levels.add(level)
                level = 0
                continue
            }

            level = level * 10 + (line[i++] - '0')
        }
        levels.add(level)

        return levels
    }

    private fun isSafe(levels: List<Int>, maxErrors: Int): Boolean {
        var errorCount = 0
        var previousLevel = levels[0]
        var previousDiff = 0
        for (i in 1 until levels.size) {
            val level = levels[i]
            val diff = level - previousLevel
            if (diff.absoluteValue !in 1..3) {
                errorCount++
                if (errorCount > maxErrors) {
                    return false
                }
                continue
            }

            if (i > 1 && diff.sign != previousDiff.sign) {
                errorCount++
                if (errorCount > maxErrors) {
                    return false
                }
                continue
            }

            previousDiff = diff
            previousLevel = level
        }

        return true
    }
}

fun main() {
    val day02 = Day02()

    day02.input = readInput("Day02_test")
    check(day02.part1() == 2)
    check(day02.part2() == 4)


    day02.input = readInput("Day02")
    check(day02.part1().also { println(it) } == 369)
    check(day02.part2().also { println(it) } == 428)
}