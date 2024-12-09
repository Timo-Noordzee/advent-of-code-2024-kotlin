package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.*
import java.util.concurrent.TimeUnit

private typealias FreeSpaceBlock = Triple<Int, Int, Int>

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class Day09 {

    var input = emptyList<String>()

    @Setup
    fun setup() {
        input = readInput("Day09")
    }

    @Benchmark
    fun part1(): Long {
        val diskMap = input.first()
        var index = 0
        var left = 0
        var right = diskMap.lastIndex
        var checksum = 0L
        var remaining = diskMap.last() - '0'

        while (left < right) {
            when (left and 1) {
                FILE -> {
                    val fileId = left / 2
                    val count = diskMap[left] - '0'
                    checksum += calculateMultiplier(index, count) * fileId
                    index += count
                }
                FREE_SPACE -> {
                    var free = diskMap[left] - '0'

                    // As long as there is free space available
                    while (free > 0) {
                        // Try to move all the remaining parts of the rightmost file to the available space
                        val blockSize = free.coerceAtMost(remaining)
                        val fileId = right / 2

                        // Add the value of the moved block to the checksum and increment/decrement index, free and remaining
                        checksum += calculateMultiplier(index, blockSize) * fileId
                        index += blockSize
                        free -= blockSize
                        remaining -= blockSize

                        // If all parts of the rightmost file have been moved, move to the next file on the left of it
                        if (remaining == 0) {
                            right -= 2
                            remaining = diskMap[right] - '0'
                        }
                    }
                }
            }
            left++
        }

        if (remaining > 0) {
            val fileId = right / 2
            checksum += calculateMultiplier(index, remaining) * fileId
        }

        return checksum
    }

    private fun calculateMultiplier(index: Int, size: Int): Long {
        val start = index - 1L
        val end = index + size - 1L
        val prefix = (start * (start + 1)) / 2
        return (end * (end + 1)) / 2 - prefix
    }

    @Benchmark
    fun part2(): Long {
        val diskMap = input.first()

        // Keep track of all available free space blocks by their size and sorted by their index
        val freeSpaceBlocks = Array(10) { PriorityQueue<FreeSpaceBlock>(compareBy { it.first }) }

        // Array used to keep track of the original start index of each file
        val startIndex = IntArray(diskMap.length)

        var index = 0
        var freeBlockSizeCounter = 0
        for (i in diskMap.indices) {
            val count = diskMap[i] - '0'
            when (i and 1) {
                FILE -> startIndex[i] = index
                FREE_SPACE -> if (count > 0) {
                    if (freeSpaceBlocks[count].isEmpty()) {
                        freeBlockSizeCounter++
                    }
                    freeSpaceBlocks[count] += Triple(i, count, index)
                }
            }
            index += count
        }

        var checksum = 0L
        var right = diskMap.lastIndex
        while (right > 0 && freeBlockSizeCounter != 0) {
            val fileId = right / 2
            val fileSize = diskMap[right] - '0'

            // Find the queue of the first available free block large enough to fit the current file
            var firstFreeSpaceBlockQueue: PriorityQueue<FreeSpaceBlock>? = null
            for (size in fileSize until 10) {
                val freeBlocks = freeSpaceBlocks[size]
                if (freeBlocks.isEmpty()) continue

                // There is no preceding free space block of size `size` available
                if (freeBlocks.first().first > right) {
                    freeSpaceBlocks[size] = PriorityQueue<FreeSpaceBlock>()
                    freeBlockSizeCounter--
                    continue
                }

                // Ensure the leftmost free space block is used
                if (firstFreeSpaceBlockQueue == null || firstFreeSpaceBlockQueue.first().first > freeBlocks.first().first) {
                    firstFreeSpaceBlockQueue = freeBlocks
                }
            }

            // If a free space has been found that can fit the current file, move it
            if (firstFreeSpaceBlockQueue != null) {
                val (left, freeSpaceCount, freeSpaceIndex) = firstFreeSpaceBlockQueue.remove()

                // Calculate the checksum effect when moving the file to the free location
                checksum += calculateMultiplier(freeSpaceIndex, fileSize) * fileId

                // If not all available free space in the block was used, add the remaining free space back to the queue
                val remainingFreeSpace = freeSpaceCount - fileSize
                if (remainingFreeSpace > 0) {
                    freeSpaceBlocks[remainingFreeSpace].add(
                        Triple(left, remainingFreeSpace, freeSpaceIndex + fileSize)
                    )
                }
            } else {
                // There is no free location available to move the file to, so calculate the checksum at the original position
                checksum += calculateMultiplier(startIndex[right], fileSize) * fileId
            }

            // Move to the next file
            right -= 2
        }

        // All remaining files can't be moved. Calculate the checksum at their original location
        while (right > 0) {
            val fileId = right / 2
            val fileSize = diskMap[right] - '0'
            checksum += calculateMultiplier(startIndex[right], fileSize) * fileId
            right -= 2
        }

        return checksum
    }

    companion object {

        const val FILE = 0
        const val FREE_SPACE = 1
    }
}

fun main() {
    val day09 = Day09()

    day09.input = readInput("Day09_test")
    check(day09.part1() == 1928L)
    check(day09.part2() == 2858L)

    day09.input = readInput("Day09")
    check(day09.part1().also { println(it) } == 6334655979668L)
    check(day09.part2().also { println(it) } == 6349492251099L)
}