class Coord3(val x: Int, val y: Int, val z: Int) {
  private val delta = listOf(-1, 1)
  val neighbours: List<Coord3>
    get() {
      val result = MutableList(0) { Coord3(0, 0, 0) }
      for (d in delta) {
        result.add(Coord3(x + d, y, z))
        result.add(Coord3(x, y + d, z))
        result.add(Coord3(x, y, z + d))
      }
      return result
    }

  override fun equals(other: Any?): Boolean {
    return other is Coord3 && x == other.x && y == other.y && z == other.z
  }

  override fun hashCode(): Int {
    return ((x to y) to z).hashCode()
  }

  override fun toString(): String {
    return "{x=$x, y=$y, z=$z}"
  }
}

fun main() {
  fun parseInput(input: List<String>): List<Coord3> {
    return input.map { it.split(",").map { it.toInt() } }.map { Coord3(it[0], it[1], it[2]) }
  }

  fun countFreeSides(cube: Coord3, valid: (Coord3) -> Boolean): Int = cube.neighbours.count { valid(it) }

  fun part1(input: List<String>): Int {
    val cubes = parseInput(input)
    val setCubes = cubes.toSet()
    return cubes.sumOf { countFreeSides(it) { !setCubes.contains(it) } }
  }

  fun markOuterWater(lava: Set<Coord3>): HashSet<Coord3> {
    val visited = HashSet<Coord3>()
    val from = lava.flatMap { listOf(it.x, it.y, it.z) }.min() - 1
    val to = lava.flatMap { listOf(it.x, it.y, it.z) }.max() + 1
    val borders = from .. to
    fun valid(cube: Coord3): Boolean = cube.x in borders && cube.y in borders && cube.z in borders && !visited.contains(cube) && !lava.contains(cube)

    val queue = MutableList(1) { Coord3(from, from, from) }

    while (queue.isNotEmpty()) {
      val current = queue.removeFirst()
      if (!valid(current)) continue

      visited.add(current)
      queue.addAll(current.neighbours.filter { valid(it) })
    }
    return visited
  }

  fun part2(input: List<String>): Int {
    val cubes = parseInput(input)
    val water = markOuterWater(cubes.toSet())
    return cubes.sumOf { countFreeSides(it) { water.contains(it) } }
  }

  val testInput = readInputLines("Day18_test")
  check(part1(testInput) == 64)
  check(part2(testInput) == 58)

  val input = readInputLines(18)
  println(part1(input))
  println(part2(input))
}