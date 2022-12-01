fun main() {
    fun part1(input: List<String>): Int {
        var result = 0
        var current = 0

        for (i in input.indices) {
            if (input[i].isBlank()) {
                result = result.coerceAtLeast(current)
                current = 0
            } else {
                current += Integer.parseInt(input[i])
            }
        }
        return result.coerceAtLeast(current)
    }

    fun part2(input: List<String>): Int {
        val calories = ArrayList<Int>(0)

        var current = 0
        for (i in input.indices) {
            if (input[i].isBlank()) {
                calories.add(current)
                current = 0
            } else {
                current += Integer.parseInt(input[i])
            }
        }
        calories.add(current)
        return calories.sortedDescending().subList(0, 3).sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    println(part1(testInput))
    println(part2(testInput))

    println()

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
