fun main() {
  class Field {
    val field = MutableList(0) { List(0) { 0 } }
    var start = Coordinate(0, 0)
    var end = Coordinate(0, 0)
    var canStep: (Coordinate, Coordinate) -> Boolean = { from, to -> field.at(from) + 1 >= field.at(to) }

    fun neighbours(current: Coordinate): List<Coordinate> {
      val result = MutableList(0) { Coordinate(0, 0) }
      for (dx in -1 .. 1) {
        for (dy in -1 .. 1) {
          if (dx == 0 && dy == 0) continue
          if (dx != 0 && dy != 0) continue
          val next = Coordinate(current.x + dx, current.y + dy)
          if (next.x in 0 until field.size && next.y in 0 until field[0].size && canStep(current, next))
              result.add(next)
        }
      }
      return result
    }

    fun bfs(
      start: Coordinate = this.start,
      isFinal: (Coordinate) -> Boolean = { x -> x == end }
    ): Int {
      val visited = List(field.size) { MutableList(field[0].size) { false } }
      visited.set(start, true)

      val queue = MutableList(1) { start to 0 }
      while (queue.isNotEmpty()) {
        val (current, dist) = queue.removeFirst()
        for (next in neighbours(current)) {
          if (isFinal(next)) return dist + 1
          if (visited.at(next)) continue
          queue.add(next to dist + 1)
          visited.set(next, true)
        }
      }
      return -1
    }
  }

  fun parseInput(input: List<String>): Field {
    val field = Field()
    field.field.addAll(input.mapIndexed { i, value ->
      value.toList().mapIndexed { j, char ->
        when (char) {
          'S' -> {
            field.start = Coordinate(i, j)
            'a' - 'a'
          }
          'E' -> {
            field.end = Coordinate(i, j)
            'z' - 'a'
          }
          else -> char - 'a'
        }
      }
    })
    return field
  }

  fun part1(input: List<String>): Int {
    val field = parseInput(input)
    return field.bfs()
  }

  fun part2(input: List<String>): Int {
    val field = parseInput(input)
    field.canStep = { from, to -> field.field.at(to) >= field.field.at(from) - 1 }
    return field.bfs(field.end) { x -> field.field.at(x) == 0 }
  }

  val testInput = readInputLines("Day12_test")
  check(part1(testInput) == 31)
  check(part2(testInput) == 29)

  val input = readInputLines(12)
  println(part1(input))
  println(part2(input))
}