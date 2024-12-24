package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class Day24 {

    var input = emptyList<String>()

    @Setup
    fun setup() {
        input = readInput("Day24")
    }

    @Benchmark
    fun part1(): Long {
        val values = mutableMapOf<String, Boolean>()
        input.takeWhile { it.isNotEmpty() }.forEach { line ->
            val (wire, value) = line.split(": ")
            values[wire] = value == "1"
        }

        val queue = ArrayDeque<Array<String>>()
        input.takeLastWhile { it.isNotEmpty() }.forEach { line ->
            val (a, operation, b, _, output) = line.split(' ')
            queue.add(arrayOf(a, b, operation, output))
        }

        while (queue.isNotEmpty()) {
            val line = queue.removeFirst()
            if (line[0] !in values || line[1] !in values) {
                queue.add(line)
                continue
            }

            val a = values.getValue(line[0])
            val b = values.getValue(line[1])
            val operation = line[2]
            val output = line[3]
            values[output] = when (operation) {
                "AND" -> a && b
                "OR" -> a || b
                "XOR" -> a.xor(b)
                else -> error("unknown operation $operation")
            }
        }

        var result = 0L
        values.forEach { (wire, value) ->
            if (wire.startsWith('z') && value) {
                val index = wire.substringAfter('z').toInt()
                result = result or (1L shl index)
            }
        }

        return result
    }

    @Benchmark
    fun part2(): String {
        return ""
    }
}

fun main() {
    val day24 = Day24()

    day24.input = readInput("Day24_test")
    check(day24.part1().also { println("test part 1: $it") } == 2024L)

    day24.input = readInput("Day24")
    check(day24.part1().also { println("part 1: $it") } == 51715173446832L)
    check(day24.part2().also { println("part 2: $it") } == "")
}