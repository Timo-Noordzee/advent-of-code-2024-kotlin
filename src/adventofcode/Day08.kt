package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.*
import java.util.concurrent.TimeUnit

private typealias Antenna = Pair<Int, Int>

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class Day08 {

    private data class AntennaMap(val m: Int, val n: Int, val antennas: Map<Char, MutableList<Antenna>>)

    var input = emptyList<String>()

    @Setup
    fun setup() {
        input = readInput("Day08")
    }

    @Benchmark
    fun part1(): Int {
        val (m, n, antennasByType) = parseMap(input)
        val seen = BitSet()
        var count = 0
        antennasByType.forEach { (_, antennas) ->
            for (i in 0 until antennas.size) {
                for (j in i + 1 until antennas.size) {
                    val (ai, aj) = antennas[i]
                    val (bi, bj) = antennas[j]

                    // The delta vector between the antennas at index i and j
                    val di = ai - bi
                    val dj = aj - bj

                    // Create antinode next to the antenna at index i
                    val an1i = ai + di
                    val an1j = aj + dj
                    if (an1i in 0 until m && an1j in 0 until n) {
                        val key = an1i * n + an1j
                        if (!seen.get(key)) {
                            seen.set(key)
                            count++
                        }
                    }

                    // Create antinode next to the antenna at index j
                    val an2i = bi - di
                    val an2j = bj - dj
                    if (an2i in 0 until m && an2j in 0 until n) {
                        val key = an2i * n + an2j
                        if (!seen.get(key)) {
                            seen.set(key)
                            count++
                        }
                    }
                }
            }
        }

        return count
    }

    @Benchmark
    fun part2(): Int {
        val (m, n, antennasByType) = parseMap(input)
        val seen = BitSet()
        var count = 0
        antennasByType.forEach { (_, antennas) ->
            for (i in 0 until antennas.size) {
                for (j in i + 1 until antennas.size) {
                    val (ai, aj) = antennas[i]
                    val (bi, bj) = antennas[j]

                    // The delta vector between the antennas at index i and j
                    val di = ai - bi
                    val dj = aj - bj

                    // Variables used to track current position while drawing a line
                    var ci = ai
                    var cj = aj

                    do {
                        val key = ci * n + cj
                        if (!seen.get(key)) {
                            seen.set(key)
                            count++
                        }

                        ci += di
                        cj += dj
                    } while (ci in 0 until m && cj in 0 until n)

                    ci = bi
                    cj = bj
                    do {
                        val key = ci * n + cj
                        if (!seen.get(key)) {
                            seen.set(key)
                            count++
                        }

                        ci -= di
                        cj -= dj
                    } while (ci in 0 until m && cj in 0 until n)
                }
            }
        }

        return count
    }

    private fun parseMap(input: List<String>): AntennaMap {
        val m = input.size
        val n = input[0].length
        val antennas = buildMap<Char, MutableList<Antenna>> {
            for (i in 0 until m) {
                val line = input[i]
                for (j in 0 until n) {
                    if (line[j] == '.') {
                        continue
                    }

                    getOrPut(line[j]) { mutableListOf() }.add(Pair(i, j))
                }
            }
        }
        return AntennaMap(m, n, antennas)
    }
}

fun main() {
    val day08 = Day08()

    day08.input = readInput("Day08_test")
    check(day08.part1() == 14)
    check(day08.part2() == 34)

    day08.input = readInput("Day08")
    check(day08.part1().also { println(it) } == 222)
    check(day08.part2().also { println(it) } == 884)
}