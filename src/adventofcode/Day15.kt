package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
class Day15 {

    var input = emptyList<String>()

    @Setup
    fun setup() {
        input = readInput("Day15")
    }

    @Benchmark
    fun part1(): Int {
        val map = input.takeWhile { it.isNotEmpty() }
        val m = map.size
        val n = map[0].length

        val walls = mutableSetOf<Pair<Int, Int>>()
        val boxes = mutableSetOf<Pair<Int, Int>>()

        var start = Pair(0, 0)
        for (i in 0 until m) {
            for (j in 0 until n) {
                when (map[i][j]) {
                    '@' -> start = Pair(i, j)
                    '#' -> walls += Pair(i, j)
                    'O' -> boxes += Pair(i, j)
                }
            }
        }

        var (i, j) = start

        fun move(di: Int, dj: Int) {
            var next = Pair(i + di, j + dj)

            // Next position is a wall, can't continue
            if (next in walls) {
                return
            }

            // Next position is unoccupied, move
            if (next !in boxes) {
                i += di
                j += dj
                return
            }

            // Find end of stack of boxes
            while (next in boxes) {
                next = Pair(next.first + di, next.second + dj)
            }

            // Stack of boxes in being pushed into a wall, can't continue
            if (next in walls) {
                return
            }

            i += di
            j += dj
            // Push stack of boxes by moving the first box to the end of the stack
            boxes -= Pair(i, j)
            boxes += next
        }

        val moves = input.drop(m + 1).joinToString()
        moves.forEach { move ->
            when (move) {
                '>' -> move(0, 1)
                '<' -> move(0, -1)
                '^' -> move(-1, 0)
                'v' -> move(1, 0)
            }
        }

        return boxes.sumOf { (i, j) -> 100 * i + j }
    }

    @Benchmark
    fun part2(): Int {
        return 0
    }
}

fun main() {
    val day15 = Day15()

    day15.input = readInput("Day15_test")
    check(day15.part1().also { println("test part 1: $it") } == 2028)
//    check(day15.part2().also { println("test part 2: $it") } == 0)

    day15.input = readInput("Day15")
    check(day15.part1().also { println("part 1: $it") } == 1412971)
//    check(day15.part2().also { println("part 2: $it") } == 0)
}