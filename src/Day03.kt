fun main() {
  fun List<List<Int>>.intersection(): Int {
    return map { it.toSet() }.reduce { a, b -> a intersect b }.sum()
  }

  fun parseInput(input: String): List<Int> {
    return input.toCharArray().asList().map { if (it.isUpperCase()) it - 'A' + 27 else it - 'a' + 1 }
  }

  fun part1(input: List<String>): Int {
    return input
      .map(::parseInput)
      .map { l -> l.chunked(l.size / 2) }
      .sumOf { it.intersection() }
  }

  fun part2(input: List<String>): Int {
    return input
      .map(::parseInput)
      .chunked(3)
      .sumOf { it.intersection() }
  }

  val testInput = readInputLines("Day03_test")
  check(part1(testInput) == 157)
  check(part2(testInput) == 70)

  val input = readInputLines(3)
  println(part1(input))
  println(part2(input))
}
