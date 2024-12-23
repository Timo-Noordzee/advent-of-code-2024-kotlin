package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
class Day22 {

    var input = emptyList<String>()

    @Setup
    fun setup() {
        input = readInput("Day22")
    }

    @Benchmark
    fun part1(): Long {
        return input.sumOf { line ->
            var secret = line.toLong()
            repeat(2_000) {
                secret = ((secret shl 6) xor secret) and 0xFFFFFF
                secret = ((secret shr 5) xor secret) and 0xFFFFFF
                secret = ((secret shl 11) xor secret) and 0xFFFFFF
            }
            secret
        }
    }

    @Benchmark
    fun part2(): Int {
        val profit = IntArray(642676)
        val lastSeen = IntArray(642676) { -1 }

        input.forEachIndexed { index, line ->
            val prices = priceSequence(line.toLong()).take(2001).toList()

            // Create a mask representing the last 4 price changes
            var changeMask = changeToMask(prices[0], prices[1]) shl 15
            changeMask = changeMask or (changeToMask(prices[1], prices[2]) shl 10)
            changeMask = changeMask or (changeToMask(prices[2], prices[3]) shl 5)
            changeMask = changeMask or changeToMask(prices[3], prices[4])

            for (i in 4 until prices.size) {
                changeMask = ((changeMask shl 5) or changeToMask(prices[i - 1], prices[i])) and 0x0FFFFF
                if (lastSeen[changeMask] != index) {
                    profit[changeMask] += (prices[i] % 10).toInt()
                    lastSeen[changeMask] = index
                }
            }
        }

        return profit.max()
    }

    private fun changeToMask(from: Long, to: Long): Int {
        val fromDigit = from % 10
        val toDigit = to % 10
        return when (val diff = (toDigit - fromDigit).toInt()) {
            in 0..9 -> diff
            in -9 until 0 -> 9 + diff.absoluteValue
            else -> error("diff of $diff is impossible")
        }
    }

    private fun priceSequence(start: Long) = generateSequence(start) {
        var secret = it
        secret = ((secret shl 6) xor secret) and 0xFFFFFF
        secret = ((secret shr 5) xor secret) and 0xFFFFFF
        ((secret shl 11) xor secret) and 0xFFFFFF
    }
}

fun main() {
    val day22 = Day22()

    day22.input = readInput("Day22_test_1")
    check(day22.part1().also { println("test part 1: $it") } == 37327623L)
    day22.input = readInput("Day22_test_2")
    check(day22.part2().also { println("test part 2: $it") } == 23)

    day22.input = readInput("Day22")
    check(day22.part1().also { println("part 1: $it") } == 20506453102L)
    check(day22.part2().also { println("part 2: $it") } == 2423)
}