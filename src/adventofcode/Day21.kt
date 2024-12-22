package adventofcode

import adventofcode.util.Point2D
import adventofcode.util.neighbors
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class Day21 {

    var input = emptyList<String>()

    @Setup
    fun setup() {
        input = readInput("Day21")
    }

    @Benchmark
    fun part1() = solve(2)

    @Benchmark
    fun part2() = solve(25)

    private fun getDirection(from: Point2D, to: Point2D): Char {
        val dx = to.x - from.x
        val dy = to.y - from.y
        return when {
            dx == 0 && dy == 1 -> 'v'
            dx == 0 && dy == -1 -> '^'
            dx == -1 && dy == 0 -> '<'
            dx == 1 && dy == 0 -> '>'
            else -> error("can't move from $from to $to")
        }
    }

    private fun Map<Point2D, Char>.findPaths(start: Point2D, target: Point2D): List<String> {
        val queue = ArrayDeque<MutableSet<Point2D>>()
        queue.add(mutableSetOf(start))

        val paths = mutableListOf<String>()

        while (queue.isNotEmpty() && paths.isEmpty()) {
            repeat(queue.size) {
                val path = queue.removeFirst()
                val location = path.last()

                if (location == target) {
                    paths.add(path.zipWithNext(::getDirection).joinToString("", postfix = "A"))
                } else {
                    location.neighbors.forEach { neighbor ->
                        if (neighbor in keys && neighbor !in path) {
                            val newPath = LinkedHashSet(path)
                            newPath.add(neighbor)
                            queue.add(newPath)
                        }
                    }
                }
            }
        }

        return paths
    }

    private fun Map<Point2D, Char>.allPaths() = buildMap<Pair<Char, Char>, List<String>> {
        this@allPaths.flatMap { from ->
            this@allPaths.map { to ->
                put(Pair(from.value, to.value), findPaths(from.key, to.key))
            }
        }
    }

    private fun solve(targetDepth: Int): Long {
        val numericPaths = mapOf(
            Point2D(0, 0) to '7',
            Point2D(1, 0) to '8',
            Point2D(2, 0) to '9',
            Point2D(0, 1) to '4',
            Point2D(1, 1) to '5',
            Point2D(2, 1) to '6',
            Point2D(0, 2) to '1',
            Point2D(1, 2) to '2',
            Point2D(2, 2) to '3',
            Point2D(1, 3) to '0',
            Point2D(2, 3) to 'A',
        ).allPaths()

        val directionalPaths = mapOf(
            Point2D(1, 0) to '^',
            Point2D(2, 0) to 'A',
            Point2D(0, 1) to '<',
            Point2D(1, 1) to 'v',
            Point2D(2, 1) to '>',
        ).allPaths()

        val cache = Array(targetDepth + 1) { mutableMapOf<String, Long>() }

        fun solve(
            code: String,
            depth: Int,
            allPaths: Map<Pair<Char, Char>, List<String>>,
        ): Long = cache[depth].getOrPut(code) {
            "A$code".zipWithNext().sumOf { move ->
                val paths = allPaths.getValue(move)
                if (depth == targetDepth) {
                    paths.first().length.toLong()
                } else {
                    paths.minOf { path -> solve(path, depth + 1, directionalPaths) }
                }
            }
        }

        return input.sumOf { code ->
            val sequenceLength = solve(code, 0, numericPaths)
            val numericCode = code.substringBeforeLast('A').toLong()
            sequenceLength * numericCode
        }
    }
}

fun main() {
    val day21 = Day21()

    day21.input = readInput("Day21_test")
    check(day21.part1().also { println("test part 1: $it") } == 126384L)

    day21.input = readInput("Day21")
    check(day21.part1().also { println("part 1: $it") } == 174124L)
    check(day21.part2().also { println("part 2: $it") } == 216668579770346L)
}