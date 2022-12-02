fun main() {
  fun part1(input: List<String>): Int {
    val shapeValue = hashMapOf(Pair('X', 1), Pair('Y', 2), Pair('Z', 3))
    val wins = hashMapOf(Pair('A', 'Y'), Pair('B', 'Z'), Pair('C', 'X'))

    var score = 0
    for (game in input) {
      val (first, second) = game.split(' ').map { it[0] }
      score += shapeValue[second] ?: 0
      if (first - 'A' == second - 'X') score += 3
      else if (wins[first] == second) score += 6
    }
    return score
  }

  fun part2(input: List<String>): Int {
    val shapeValue = hashMapOf(Pair('A', 1), Pair('B', 2), Pair('C', 3))
    val policyValue = hashMapOf(Pair('X', 0), Pair('Y', 3), Pair('Z', 6))
    val policy = hashMapOf(
      Pair('X', hashMapOf(Pair('A', 'C'), Pair('B', 'A'), Pair('C', 'B'))),
      Pair('Y', hashMapOf(Pair('A', 'A'), Pair('B', 'B'), Pair('C', 'C'))),
      Pair('Z', hashMapOf(Pair('A', 'B'), Pair('B', 'C'), Pair('C', 'A')))
    )

    var score = 0
    for (game in input) {
      val (first, move) = game.split(' ').map { it[0] }
      val second = policy[move]?.get(first) ?: 'A'
      score += (shapeValue[second] ?: 0) + (policyValue[move] ?: 0)
    }
    return score
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("Day02_test")
  println(part1(testInput))
  println(part2(testInput))

  println()

  val input = readInput(2)
  println(part1(input))
  println(part2(input))
}
