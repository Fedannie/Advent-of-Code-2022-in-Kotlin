fun main() {
  fun String.extractNumbers(): List<Int> {
    return split(' ').filter { word -> !word.any { char: Char -> !char.isDigit() } }.map { it.toInt() }
  }

  fun rotate(n: Int, input: List<List<Char>>): MutableList<MutableList<Char>> {
    val stacks = MutableList(n) { MutableList(0) { ' ' } }
    input.forEach { row ->
      row.forEachIndexed { index, crane -> if (crane != ' ') stacks[index] += crane }
    }
    return stacks
  }

  class Move(val count: Int, val from: Int, val to: Int)

  class Map(n: Int, rows: List<List<Char>>) {
    val stacks = rotate(n, rows)

    fun perform(move: Move, onceAtTime: Boolean) {
      val toMove = stacks[move.from].subList(0, move.count)
      if (onceAtTime) toMove.reverse()
      stacks[move.to].addAll(0, toMove)
      stacks[move.from] = stacks[move.from].drop(move.count).toMutableList()
    }

    fun getState(): String {
      return stacks.map { it[0] }.joinToString("")
    }
  }

  fun parseInput(input: String): Pair<Map, List<Move>> {
    val (mapStr, movesStr) = input.split("\n\n")

    val rows = mapStr.split('\n')
    val parsedRows = rows
      .dropLast(1)
      .map {
        it
          .toCharArray()
          .toList()
          .chunked(4)
          .map { it[1] }
      }
    val map = Map(rows.last().split("   ").count(), parsedRows)

    val moves = movesStr
      .split('\n')
      .map { str ->
        val numbers = str.extractNumbers()
        Move(numbers[0], numbers[1] - 1, numbers[2] - 1)
      }

    return map to moves
  }

  fun part1(input: String): String {
    val (map, moves) = parseInput(input)
    moves.forEach { move -> map.perform(move, true) }
    return map.getState()
  }

  fun part2(input: String): String {
    val (map, moves) = parseInput(input)
    moves.forEach { move -> map.perform(move, false) }
    return map.getState()
  }

  val testInput = readInput("Day05_test")
  check(part1(testInput) == "CMZ")
  check(part2(testInput) == "MCD")

  val input = readInput(5)
  println(part1(input))
  println(part2(input))
}
