package adventofcode

import adventofcode.util.TrieNode
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class Day19 {

    var input = emptyList<String>()

    @Setup
    fun setup() {
        input = readInput("Day19")
    }

    @Benchmark
    fun part1(): Int {
        val root = buildTrie(input.first())
        return input.drop(2).count { countPossibleWays(root, it) > 0 }
    }

    @Benchmark
    fun part2(): Long {
        val root = buildTrie(input.first())
        return input.drop(2).sumOf { countPossibleWays(root, it) }
    }

    private fun countPossibleWays(root: TrieNode, design: String): Long {
        val dp = LongArray(design.length + 1)

        for (start in design.length downTo 0) {
            var count = 0L
            var node = root
            for (end in start until design.length) {
                node = node[design[end]] ?: break

                if (node.isEnd) {
                    if (end == design.lastIndex) {
                        count++
                    } else {
                        count += dp[end + 1]
                    }
                }
            }

            dp[start] = count
        }

        return dp[0]
    }

    private fun buildTrie(input: String): TrieNode {
        val root = TrieNode()
        input.split(", ").forEach { pattern ->
            var node = root
            pattern.forEach { char ->
                if (char !in node) {
                    node[char] = TrieNode()
                }
                node = node[char]!!
            }
            node.isEnd = true
        }
        return root
    }
}

fun main() {
    val day19 = Day19()

    day19.input = readInput("Day19_test")
    check(day19.part1().also { println("test part 1: $it") } == 6)
    check(day19.part2().also { println("test part 2: $it") } == 16L)

    day19.input = readInput("Day19")
    check(day19.part1().also { println("part 1: $it") } == 304)
    check(day19.part2().also { println("part 2: $it") } == 705756472327497L)
}