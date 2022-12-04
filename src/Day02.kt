fun main() {
  fun parseInput(input: String): List<Char> {
    return input.split(' ').map { it[0] }
  }

  fun part1(input: List<String>): Int {
    val shapeValue = hashMapOf(Pair('X', 1), Pair('Y', 2), Pair('Z', 3))
    val wins = hashMapOf(Pair('A', 'Y'), Pair('B', 'Z'), Pair('C', 'X'))

    return input
      .map(::parseInput)
      .sumOf { (first, second) ->
        val value = shapeValue[second] ?: 0
        if (first - 'A' == second - 'X') 3 + value
        else if (wins[first] == second) 6 + value
        else value
      }
  }

  fun part2(input: List<String>): Int {
    val shapeValue = hashMapOf(Pair('A', 1), Pair('B', 2), Pair('C', 3))
    val policyValue = hashMapOf(Pair('X', 0), Pair('Y', 3), Pair('Z', 6))
    val policy = hashMapOf(
      Pair('X', hashMapOf(Pair('A', 'C'), Pair('B', 'A'), Pair('C', 'B'))),
      Pair('Y', hashMapOf(Pair('A', 'A'), Pair('B', 'B'), Pair('C', 'C'))),
      Pair('Z', hashMapOf(Pair('A', 'B'), Pair('B', 'C'), Pair('C', 'A')))
    )

    return input
      .map(::parseInput)
      .sumOf { (first, move) -> (shapeValue[policy[move]?.get(first) ?: 'A'] ?: 0) + (policyValue[move] ?: 0) }
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInputLines("Day02_test")
  check(part1(testInput) == 15)
  check(part2(testInput) == 12)

  val input = readInputLines(2)
  println(part1(input))
  println(part2(input))
}
