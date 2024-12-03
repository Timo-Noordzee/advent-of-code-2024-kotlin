package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day03 {

    var input = emptyList<String>()

    @Setup
    fun setup() {
        input = readInput("Day03")
    }

    @Benchmark
    fun part1() = input.sumOf(this::scan)

    @Benchmark
    fun part2(): Int {
        val fullInput = input.joinToString("")
        return fullInput.splitToSequence("do()").sumOf { scan(it.substringBefore("don't()")) }
    }

    private fun scan(memory: String): Int {
        var score = 0
        var index = 0

        // Manual scan instead of using regex
        input@ while (index < memory.length) {
            if (memory[index] == 'm') {
                if (memory.startsWith("mul(", index)) {
                    // Skip the "mul(" prefix
                    index += 4

                    // The next char should be the first digit of x to be a valid mul instruction
                    if (!memory[index].isDigit()) {
                        continue
                    }

                    // Parse the first number (x) of at most 3 digits
                    var x = memory[index++] - '0'
                    x@ for (j in 0 until 2) {
                        if (memory[index].isDigit()) {
                            x = x * 10 + (memory[index++] - '0')
                        } else if (memory[index] == ',') {
                            break@x
                        } else {
                            continue@input
                        }
                    }

                    // Number x and y should be separated by a comma to be a valid mul instruction
                    if (memory[index++] != ',') {
                        continue
                    }

                    // Parse the second number (y) of at most 3 digits
                    var y = memory[index++] - '0'
                    y@ for (j in 0 until 2) {
                        if (memory[index].isDigit()) {
                            y = y * 10 + (memory[index++] - '0')
                        } else if (memory[index] == ')') {
                            break@y
                        } else {
                            continue@input
                        }
                    }

                    // After number x and y the mul instruction should end with ")" to be valid
                    if (memory[index] != ')') {
                        continue
                    }

                    score += x * y
                }
            }
            index++
        }

        return score
    }
}

fun main() {
    val day03 = Day03()

    day03.input = readInput("Day03_test_1")
    check(day03.part1() == 161)
    day03.input = readInput("Day03_test_2")
    check(day03.part2() == 48)


    day03.input = readInput("Day03")
    check(day03.part1().also { println(it) } == 189527826)
    check(day03.part2().also { println(it) } == 63013756)
}