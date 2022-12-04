fun main() {
  fun part1(input: List<String>): Int {
    return input
      .map { s -> s.toCharArray().asList().map { if (it.isUpperCase()) it - 'A' + 27 else it - 'a' + 1 } }
      .map { l -> listOf(l.subList(0, l.size / 2).toSet(), l.subList(l.size / 2, l.size).toSet()) }
      .map { p -> p[0].intersect(p[1]).sum() }
      .sum()
  }

  fun part2(input: List<String>): Int {
    return input
      .map { s -> s.toCharArray().asList().map { if (it.isUpperCase()) it - 'A' + 27 else it - 'a' + 1 } }
      .chunked(3)
      .map { l -> l.reduceRight { a, b -> a.toSet().intersect(b.toSet()).toList() }.sum() }
      .sum()
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("Day03_test")
  println(part1(testInput))
  println(part2(testInput))

  println()

  val input = readInput(3)
  println(part1(input))
  println(part2(input))
}
