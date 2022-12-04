fun main() {
    fun parseInput(input: String): List<Int> {
        return input
            .split("\n\n")
            .map { it.split('\n').map { it.toInt() }.sum() }
    }

    fun part1(input: String): Int = parseInput(input).max()

    fun part2(input: String): Int = parseInput(input).sortedDescending().subList(0, 3).sum()

    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = readInput(1)
    println(part1(input))
    println(part2(input))
}
