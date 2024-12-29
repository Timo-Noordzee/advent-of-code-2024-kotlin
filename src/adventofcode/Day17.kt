package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

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
        val a = input[0].substringAfterLast(' ').toLong()
        val b = input[1].substringAfterLast(' ').toLong()
        val c = input[2].substringAfterLast(' ').toLong()
        val program = input[4].substringAfter(' ').split(',').map { it.toInt() }
        return runProgram(a, b, c, program).joinToString(",")
    }

    @Benchmark
    fun part2(): Long {
        val b = input[1].substringAfterLast(' ').toLong()
        val c = input[2].substringAfterLast(' ').toLong()
        val program = input[4].substringAfter(' ').split(',').map { it.toInt() }

        fun findA(target: List<Int>): List<Long> {
            val prefixes = if (target.size == 1) listOf(0L) else findA(target.drop(1)).map { it shl 3 }
            return prefixes.flatMap { prefix ->
                (0 until 8).map { prefix + it }.filter { a -> runProgram(a, b, c, program) == target }
            }
        }

        return findA(program).min()
    }

    @Suppress("NAME_SHADOWING")
    private fun runProgram(a: Long, b: Long, c: Long, program: List<Int>): List<Int> {
        var a = a
        var b = b
        var c = c
        var instructionPointer = 0

        val output = mutableListOf<Int>()

        while (true) {
            val opcode = program[instructionPointer++]
            val operand = program[instructionPointer++]
            val comboOperand = when (operand) {
                in 0 until 4 -> operand.toLong()
                4 -> a
                5 -> b
                6 -> c
                else -> error("impossible value ($operand) for combo operand")
            }

            when (opcode) {
                ADV -> a = a shr comboOperand.toInt()
                BXL -> b = b xor operand.toLong()
                BST -> b = comboOperand and 7
                JNZ -> if (a != 0L) instructionPointer = operand else break
                BXC -> b = b xor c
                OUT -> output += (comboOperand and 7).toInt()
                BDV -> b = a shr comboOperand.toInt()
                CDV -> c = a shr comboOperand.toInt()
            }
        }

        return output
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
    check(day17.part2().also { println("test part 2: $it") } == 117440L)

    day17.input = readInput("Day17")
    check(day17.part1().also { println("part 1: $it") } == "7,4,2,5,1,4,6,0,4")
    check(day17.part2().also { println("part 2: $it") } == 164278764924605L)
}