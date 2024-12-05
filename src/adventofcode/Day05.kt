package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day05 {

    var input = emptyList<String>()

    @Setup
    fun setup() {
        input = readInput("Day05")
    }

    @Benchmark
    fun part1() = solve { (original, corrected) -> original == corrected }

    @Benchmark
    fun part2() = solve { (original, corrected) -> original != corrected }

    private infix fun Int.merge(other: Int): Int = or(other shl 7)

    private inline fun solve(filter: (pair: Pair<List<Int>, List<Int>>) -> Boolean): Int {
        val (rules, pages) = parseInput(input)

        val rulesSet = BitSet()
        rules.forEach { (x, y) -> rulesSet.set(x merge y) }

        val comparator = Comparator<Int> { x, y ->
            when {
                rulesSet.get(x merge y) -> -1
                rulesSet.get(y merge x) -> 1
                else -> 0
            }
        }

        return pages
            .map { it to it.sortedWith(comparator) }
            .filter(filter)
            .sumOf { (_, corrected) -> corrected[corrected.size / 2] }
    }

    private fun parseInput(input: List<String>): Pair<List<Pair<Int, Int>>, List<List<Int>>> {
        var i = 0
        val rules = mutableListOf<Pair<Int, Int>>()
        while (input[i].isNotEmpty()) {
            val line = input[i]
            val x = (line[0] - '0') * 10 + (line[1] - '0')
            val y = (line[3] - '0') * 10 + (line[4] - '0')
            rules += Pair(x, y)
            i++
        }

        val pages = mutableListOf<List<Int>>()
        while (++i < input.size) {
            val line = input[i]
            pages += line.split(',').map { it.toInt() }
        }

        return Pair(rules, pages)
    }
}

fun main() {
    val day05 = Day05()

    day05.input = readInput("Day05_test")
    check(day05.part1() == 143)
    check(day05.part2() == 123)

    day05.input = readInput("Day05")
    check(day05.part1().also { println(it) } == 5374)
    check(day05.part2().also { println(it) } == 4260)
}