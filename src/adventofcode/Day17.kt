package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import kotlin.math.pow

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class Day17 {

    var input = emptyList<String>()

    @Setup
    fun setup() {
        input = readInput("Day17")
    }

    @Benchmark
    fun part1(): String {
        val program = input[4].substringAfter(' ').split(',').map { it.toInt() }
        var a = input[0].substringAfterLast(' ').toInt()
        var b = input[1].substringAfterLast(' ').toInt()
        var c = input[2].substringAfterLast(' ').toInt()
        var instructionPointer = 0

        fun literalOperand() = program[instructionPointer++]

        fun comboOperand() = when (val value = program[instructionPointer++]) {
            in 0 until 4 -> value
            4 -> a
            5 -> b
            6 -> c
            else -> error("impossible value ($value) for combo operand")
        }

        val output = mutableListOf<Int>()

        while (instructionPointer < program.size) {
            when (program[instructionPointer++]) {
                ADV -> a /= (2.0).pow(comboOperand()).toInt()
                BXL -> b = b xor literalOperand()
                BST -> b = comboOperand() and 7
                JNZ -> if (a != 0) instructionPointer = literalOperand() else instructionPointer++
                BXC -> {
                    b = b xor c
                    instructionPointer++
                }
                OUT -> output.add(comboOperand() and 7)
                BDV -> b = a / (2.0).pow(comboOperand()).toInt()
                CDV -> c = a / (2.0).pow(comboOperand()).toInt()
            }
        }

        return output.joinToString(",")
    }

    @Benchmark
    fun part2(): Int {
        return 0
    }

    companion object {

        const val ADV = 0
        const val BXL = 1
        const val BST = 2
        const val JNZ = 3
        const val BXC = 4
        const val OUT = 5
        const val BDV = 6
        const val CDV = 7
    }
}

fun main() {
    val day17 = Day17()

    day17.input = readInput("Day17_test_1")
    check(day17.part1().also { println("test part 1: $it") } == "4,6,3,5,6,3,5,2,1,0")
    day17.input = readInput("Day17_test_2")
    check(day17.part2().also { println("test part 2: $it") } == 0)

    day17.input = readInput("Day17")
    check(day17.part1().also { println("part 1: $it") } == "7,4,2,5,1,4,6,0,4")
    check(day17.part2().also { println("part 2: $it") } == 0)
}