fun main() {
  fun parseInput(input: String): List<IntRange> {
    return input.split(',')
      .map { it.split('-').map { it.toInt() } }
      .sortedWith(compareBy<List<Int>> { it[0] }.thenByDescending { it[1] - it[0] })
      .map { it[0] .. it[1] }
  }

  fun part1(input: List<String>): Int {
    return input.map(::parseInput).count { pair -> pair[1].last in pair[0] }
  }

  fun part2(input: List<String>): Int {
    return input.map(::parseInput).count { pair -> pair[0].last >= pair[1].first }
  }

  val testInput = readInputLines("Day04_test")
  check(part1(testInput) == 2)
  check(part2(testInput) == 4)

  val input = readInputLines(4)
  println(part1(input))
  println(part2(input))
}