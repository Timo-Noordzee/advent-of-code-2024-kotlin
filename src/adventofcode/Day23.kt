package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
class Day23 {

    var input = emptyList<String>()

    @Setup
    fun setup() {
        input = readInput("Day23")
    }

    @Benchmark
    fun part1(): Int {
        val graph = buildGraph()
        return graph.keys
            .filter { it.startsWith('t') }
            .flatMapTo(mutableSetOf()) { start ->
                val connected = graph.getValue(start).toList()
                connected.flatMapIndexed { index, b ->
                    connected.subList(index + 1, connected.size).mapNotNull { c ->
                        if (c in graph.getValue(b)) listOf(start, b, c).sorted() else null
                    }
                }
            }
            .size
    }

    @Benchmark
    fun part2(): String {
        val graph = buildGraph()

        fun bronKerbosch(r: Set<String>, p: Set<String>, x: Set<String>): Set<String> {
            return if (p.isEmpty() && x.isEmpty()) r else {
                val u = (p + x).maxBy { graph.getValue(it).size }
                (p - graph.getValue(u)).map { v ->
                    bronKerbosch(
                        r + v,
                        p intersect graph.getValue(v),
                        x intersect graph.getValue(v)
                    )
                }.maxBy { it.size }
            }
        }

        return bronKerbosch(emptySet(), graph.keys, emptySet()).sorted().joinToString(",")
    }

    private fun buildGraph(): Map<String, MutableSet<String>> {
        val graph = mutableMapOf<String, MutableSet<String>>()
        input.forEach { line ->
            val a = line.substringBefore('-')
            val b = line.substringAfter('-')
            graph.getOrPut(a) { mutableSetOf() }.add(b)
            graph.getOrPut(b) { mutableSetOf() }.add(a)
        }
        return graph
    }
}

fun main() {
    val day23 = Day23()

    day23.input = readInput("Day23_test")
    check(day23.part1().also { println("test part 1: $it") } == 7)
    check(day23.part2().also { println("test part 2: $it") } == "co,de,ka,ta")

    day23.input = readInput("Day23")
    check(day23.part1().also { println("part 1: $it") } == 1344)
    check(day23.part2().also { println("part 2: $it") } == "ab,al,cq,cr,da,db,dr,fw,ly,mn,od,py,uh")
}