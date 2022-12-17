enum class Shape(val bottom: List<Int>, val heights: List<Int>) {
  H_LINE(listOf(0, 0, 0, 0), listOf(1, 1, 1, 1)),
  CROSS(listOf(1, 0, 1), listOf(2, 3, 2)),
  L(listOf(0, 0, 0), listOf(1, 1, 3)),
  V_LINE(listOf(0), listOf(4)),
  SQUARE(listOf(0, 0), listOf(2, 2));

  val rightBorder: Int
    get() = bottom.size - 1
}

class TetrisField(private val width: Int = 7, private val instructions: BooleanArray) {
  private val field = Array(width) { HashSet<Int>() }
  val height: Int
    get() = this.field.maxOf { it.maxOrNull() ?: 0 }

  var index = 0

  init {
    field.forEach { it.add(0) }
  }

  val horizontals = HashMap<Int, Int>()

  private fun touch(shape: Shape, position: Coordinate): Boolean {
    for (i in 0 .. shape.rightBorder) {
      if (field[position.x + i].contains(position.y + shape.bottom[i] - 1)) return true
    }
    return false
  }

  private fun fix(shape: Shape, position: Coordinate): Boolean {
    if (!touch(shape, position)) return false
    for (i in 0 .. shape.rightBorder)
      for (j in shape.bottom[i] until shape.heights[i])
        field[position.x + i].add(position.y + j)
    if (shape == Shape.H_LINE) horizontals[position.y] = horizontals.size
    return true
  }

  private fun moveLeft(shape: Shape, position: Coordinate): Coordinate {
    if (position.x == 0) return position

    when (shape) {
      Shape.H_LINE -> {
        return if (field[position.x - 1].contains(position.y))
          position
        else
          Coordinate(position.x - 1, position.y)
      }
      Shape.CROSS -> {
        if (field[position.x - 1].contains(position.y + 1) || field[position.x].contains(position.y) || field[position.x].contains(position.y + 2))
          return position
        return Coordinate(position.x - 1, position.y)
      }
      Shape.L -> {
        return if (field[position.x - 1].contains(position.y) || field[position.x + 1].contains(position.y + 1) || field[position.x + 1].contains(position.y + 2))
          position
        else
          Coordinate(position.x - 1, position.y)
      }
      Shape.V_LINE -> {
        for (i in 0 .. 3)
          if (field[position.x - 1].contains(position.y + i))
            return position
        return Coordinate(position.x - 1, position.y)
      }
      Shape.SQUARE -> {
        for (i in 0 .. 1)
          if (field[position.x - 1].contains(position.y + i))
            return position
        return Coordinate(position.x - 1, position.y)
      }
    }
  }

  private fun moveRight(shape: Shape, position: Coordinate): Coordinate {
    if (position.x + shape.rightBorder == width - 1) return position

    when (shape) {
      Shape.H_LINE -> {
        return if (field[position.x + shape.rightBorder + 1].contains(position.y))
          position
        else
          Coordinate(position.x + 1, position.y)
      }
      Shape.CROSS -> {
        if (field[position.x + shape.rightBorder + 1].contains(position.y + 1) || field[position.x + shape.rightBorder].contains(position.y) || field[position.x + shape.rightBorder].contains(position.y + 2))
          return position
        return Coordinate(position.x + 1, position.y)
      }
      Shape.L -> {
        for (i in 0 .. 2)
          if (field[position.x + shape.rightBorder + 1].contains(position.y + i))
            return position
        return Coordinate(position.x + 1, position.y)
      }
      Shape.V_LINE -> {
        for (i in 0 .. 3)
          if (field[position.x + shape.rightBorder + 1].contains(position.y + i))
            return position
        return Coordinate(position.x + 1, position.y)
      }
      Shape.SQUARE -> {
        for (i in 0 .. 1)
          if (field[position.x + shape.rightBorder + 1].contains(position.y + i))
            return position
        return Coordinate(position.x + 1, position.y)
      }
    }
  }

  private fun move(shape: Shape, position: Coordinate, left: Boolean): Coordinate =
    if (left) moveLeft(shape, position) else moveRight(shape, position)

  fun add(shape: Shape) {
    var position = Coordinate(2, height + 4)
    while (true) {
      position = move(shape, position, instructions[index++])
      index %= instructions.size
      if (!fix(shape, position))
        position = Coordinate(position.x, position.y - 1)
      else break
    }
  }

  override fun toString(): String {
    val s = StringBuffer()
    for (i in height downTo 0) {
      s.append(field.joinToString("") { if (it.contains(i)) "#" else "." })
      s.append('\n')
    }
    return s.toString()
  }

  fun clear() {
    field.forEach { it.clear(); it.add(0) }
  }

  fun findCycle(): Pair<Long, Long> {
    val s = toString()
    val l = 30 * 8
    val i = s.substring(l).replace(s.substring(0, l), "*").indexOf('*')
    println(i)
    return (horizontals[(s.length) / 8 - 1]!! * 5 - (horizontals[(s.length - i - l) / 8 - 1]!!) * 5).toLong() to (((i + l)) / 8).toLong()
  }
}

fun main() {
  fun parseInput(input: String): BooleanArray = input.toList().map { it == '<' }.toBooleanArray()

  fun part1(input: String): Int {
    val field = TetrisField(7, parseInput(input))
    for (i in 0 until 2022) field.add(Shape.values()[i % 5])
    return field.height
  }

  fun part2(input: String): Long {
    val field = TetrisField(7, parseInput(input))
    val last = 5930
    for (i in 0 .. last) {
      field.add(Shape.values()[i % 5])
    }
    val (cycleLength, cycleHeight) = field.findCycle()
    val result = field.height + ((1000000000000 - last - 1) / cycleLength) * cycleHeight
    field.clear()
    for (j in 1 .. (1000000000000 - last - 1) % cycleLength) {
      field.add(Shape.values()[(j % 5).toInt()])
    }
    return result + field.height
  }

  val testInput = readInputLines("Day17_test")[0]
  check(part1(testInput) == 3068)
  check(part2(testInput) == 1514285714288)

  val input = readInputLines(17)[0]
  println(part1(input))
  println(part2(input))
}
