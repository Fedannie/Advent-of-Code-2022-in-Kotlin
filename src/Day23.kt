enum class Direction(val xRange: IntRange, val yRange: IntRange) {
  N(-1 .. -1, -1 .. 1), S(1 .. 1, -1 .. 1), W(-1 .. 1, -1 .. -1), E(-1 .. 1, 1 .. 1);

  fun next(coordinate: Coordinate): Coordinate {
    return when (this) {
      N -> Coordinate(coordinate.x - 1, coordinate.y)
      S -> Coordinate(coordinate.x + 1, coordinate.y)
      W -> Coordinate(coordinate.x, coordinate.y - 1)
      E -> Coordinate(coordinate.x, coordinate.y + 1)
    }
  }
}

class Elf(var coordinate: Coordinate) {
  companion object {
    val directions = Direction.values()
  }

  var i = 0

  override fun hashCode(): Int {
    return coordinate.hashCode()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Elf

    if (coordinate != other.coordinate) return false

    return true
  }
}

fun main() {
  fun parseInput(input: List<String>): HashSet<Elf> {
    val emptyCoordinate = Coordinate(-1, -1)
    return input
      .flatMapIndexed { i, s -> s.toList().mapIndexed { j, c -> if (c == '#') Coordinate(i, j) else emptyCoordinate } }
      .filter { it != emptyCoordinate }
      .map { Elf(it) }
      .toHashSet()
  }

  fun countNeighbours(cur: Elf, elves: HashSet<Elf>, direction: Direction? = null): Int {
    var result = 0
    for (dx in direction?.xRange ?: -1 .. 1) {
      for (dy in direction?.yRange ?: -1 .. 1) {
        if (dx == 0 && dy == 0) continue
        if (elves.count { it.coordinate == Coordinate(cur.coordinate.x + dx, cur.coordinate.y + dy)} > 0)
          result += 1
      }
    }
    return result
  }

  fun proposal(cur: Elf, elves: HashSet<Elf>): Coordinate? {
    for (i in 0 .. 4) {
      val direction = Elf.directions[(cur.i + i + 3) % 4]
      if (countNeighbours(cur, elves, direction) == 0) return direction.next(cur.coordinate)
    }
    return null
  }

  fun setNewCoordinate(cur: Elf, mapping: List<Pair<Elf, Coordinate>>): Boolean {
    val elfProposal = mapping.find { it.first == cur } ?: return false
    if (mapping.count { it.second == elfProposal.second } > 1) return false
    cur.coordinate = elfProposal.second
    return true
  }

  fun step(previous: HashSet<Elf>): Boolean {
    val proposals = previous
      .map {
        it.i = (it.i + 1) % 4
        it to countNeighbours(it, previous)
      }
      .filter { it.second > 0 }
      .map { it.first to proposal(it.first, previous) }
      .filter { it.second != null } as List<Pair<Elf, Coordinate>>
    return previous.map { oldElf -> setNewCoordinate(oldElf, proposals) }.any { it }
  }

  fun countEmptyTiles(elves: HashSet<Elf>): Int {
    val coordinates = elves.map { it.coordinate }
    return (coordinates.maxOf { it.x } - coordinates.minOf { it.x } + 1) *
        (coordinates.maxOf { it.y } - coordinates.minOf { it.y } + 1) - coordinates.size
  }

  fun print(elves: HashSet<Elf>) {
    val coordinates = elves.map { it.coordinate }
    for (x in coordinates.minOf { it.x } .. coordinates.maxOf { it.x }) {
      for (y in coordinates.minOf { it.y } .. coordinates.maxOf { it.y }) {
        if (coordinates.contains(Coordinate(x, y))) print('#')
        else print('.')
      }
      println()
    }
    println()
  }

  fun part1(input: List<String>): Int {
    val positions = parseInput(input)
    repeat(10) {
      step(positions)
    }
    return countEmptyTiles(positions)
  }

  fun part2(input: List<String>): Int {
    val positions = parseInput(input)
    var rounds = 1
    while (step(positions)) rounds++
    return rounds
  }

  val testInput = readInputLines("Day23_test")
  check(part1(testInput) == 110)
  check(part2(testInput) == 20)

  val input = readInputLines(23)
  println(part1(input))
  println(part2(input))
}