package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day04 {

    var input = emptyList<String>()

    @Setup
    fun setup() {
        input = readInput("Day04")
    }

    @Benchmark
    fun part1(): Int {
        return countHorizontal(input) + countVertical(input) + countDiagonal(input) + countDiagonal(input.map { it.reversed() })
    }

    @Benchmark
    fun part2(): Int {
        val m = input.size
        val n = input[0].length
        var count = 0
        for (i in 1 until (m - 1)) {
            for (j in 1 until (n - 1)) {
                if (input[i][j] == 'A') {
                    var mask = (input[i - 1][j - 1]) - 'A'
                    mask = mask shl 8
                    mask += (input[i - 1][j + 1]) - 'A'
                    mask = mask shl 8
                    mask += (input[i + 1][j - 1]) - 'A'
                    mask = mask shl 8
                    mask += (input[i + 1][j + 1]) - 'A'
                    if (mask == 202509330 || mask == 202117650 || mask == 303172620 || mask == 302780940) {
                        count++
                    }
                }
            }
        }

        return count
    }

    private fun countHorizontal(input: List<String>): Int {
        var count = 0
        input.forEach { line ->
            // Use an int mask to track the previous 4 characters (4 bytes)
            // The bit assignment that is used is (MSB) AAAAAAAA BBBBBBBB CCCCCCCC DDDDD
            // A is the char at index 'i-3'
            // B is the char at index 'i-2'
            // C is the char at index 'i-1'
            // D is the char at index 'i'
            var mask = 0
            for (i in line.indices) {
                // Shifting the mask 8 bits to the left to clear the char at index 'i-4'
                mask = mask shl 8
                // Add the char at index 'i' to the mask
                mask += line[i] - 'A'

                // If the mask equals either 386662418 (XMAS) or 301992983 (SAMX) increment count by one
                if (mask == 386662418 || mask == 301992983) {
                    count++
                }
            }
        }
        return count
    }

    private fun countVertical(input: List<String>): Int {
        val m = input.size
        val rotated = input[0].indices.map { j ->
            val stringBuilder = StringBuilder()
            stringBuilder.ensureCapacity(m)

            for (i in 0 until m) {
                stringBuilder.append(input[i][j])
            }

            stringBuilder.toString()
        }

        return countHorizontal(rotated)
    }

    private fun countDiagonal(input: List<String>): Int {
        val m = input.size
        val n = input[0].length
        assert(m == n) { "expected m and n to be of equal length" }

        val transformed = mutableListOf<String>()
        for (k in 0 until m * 2) {
            val stringBuilder = StringBuilder()
            for (j in 0..k) {
                val i = k - j
                if (i < m && j < m) {
                    stringBuilder.append(input[i][j])
                }
            }
            transformed.add(stringBuilder.toString())
        }

        return countHorizontal(transformed)
    }
}

fun main() {
    val day04 = Day04()

    day04.input = readInput("Day04_test")
    check(day04.part1() == 18)
    check(day04.part2() == 9)

    day04.input = readInput("Day04")
    check(day04.part1().also { println(it) } == 2639)
    check(day04.part2().also { println(it) } == 2005)
}