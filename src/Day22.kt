class Link(val orientation: Orientation, val newCoordinate: (Int) -> Coordinate)

enum class Orientation(val code: Int, val character: Char, val dx: Int, val dy: Int) {
  RIGHT(0, '>', 0, 1), DOWN(1, 'v', 1, 0), LEFT(2, '<', 0, -1), UP(3, '^', -1, 0);

  val next: Orientation
    get() {
      return when (this) {
        RIGHT -> DOWN
        DOWN -> LEFT
        LEFT -> UP
        UP -> RIGHT
      }
    }
  val prev: Orientation
    get() {
      return when (this) {
        RIGHT -> UP
        DOWN -> RIGHT
        LEFT -> DOWN
        UP -> LEFT
      }
    }

  fun nextFace(face: Int): Link {
    return when (this) {
      RIGHT -> {
        when (face) {
          1 -> Link(RIGHT) { x -> Coordinate(x, 100) }
          2 -> Link(LEFT) { x -> Coordinate(149 - x, 99) }
          3 -> Link(UP) { x -> Coordinate(49, 100 + x) }
          4 -> Link(RIGHT) { x -> Coordinate(100 + x, 50) }
          5 -> Link(LEFT) { x -> Coordinate(49 - x, 149) }
          else -> Link(UP) { x -> Coordinate(149, 50 + x)  }
        }
      }
      DOWN -> {
        when (face) {
          1 -> Link(DOWN) { x -> Coordinate(50, 50 + x) }
          2 -> Link(LEFT) { x -> Coordinate(50 + x, 99) }
          3 -> Link(DOWN) { x -> Coordinate(100, 50 + x) }
          4 -> Link(DOWN) { x -> Coordinate(150, x) }
          5 -> Link(LEFT) { x -> Coordinate(150 + x, 49) }
          else -> Link(DOWN) { x -> Coordinate(0, 100 + x) }
        }
      }
      LEFT -> {
        when (face) {
          1 -> Link(RIGHT) { x -> Coordinate(149 - x, 0) }
          2 -> Link(LEFT) { x -> Coordinate(x, 99) }
          3 -> Link(DOWN) { x -> Coordinate(100, x) }
          4 -> Link(RIGHT) { x -> Coordinate(49 - x, 50) }
          5 -> Link(LEFT) { x -> Coordinate(100 + x, 49) }
          else -> Link(DOWN) { x -> Coordinate(0, 50 + x) }
        }
      }
      UP -> {
        when (face) {
          1 -> Link(RIGHT) { x -> Coordinate(150 + x, 0) }
          2 -> Link(UP) { x -> Coordinate(199, x) }
          3 -> Link(UP) { x -> Coordinate(49, 50 + x) }
          4 -> Link(RIGHT) { x -> Coordinate(50 + x, 50) }
          5 -> Link(UP) { x -> Coordinate(100, 50 + x) }
          else -> Link(UP) { x -> Coordinate(150, x) }
        }
      }
    }
  }

  fun getX(coordinate: Coordinate): Int {
    return when (this) {
      RIGHT, LEFT -> coordinate.x % 50
      DOWN, UP -> coordinate.y % 50
    }
  }
}


class MonkeyMap(private val cubeSize: Int) {
  var map = List(0) { "" }
    set(value) {
      val length = value.maxOf { it.length }
      field = value.map { if (it.length < length) it + " ".repeat(length - it.length) else it }
      debugMap = value.map { it.toMutableList() }
      rowIndices = field.map { it.indexOfFirst { it != ' ' } .. it.indexOfLast { it != ' ' } }
      val colIndicesTmp = MutableList(length) { 0 .. 1 }
      for (i in colIndicesTmp.indices) {
        val col = field.map { it[i] }
        colIndicesTmp[i] = col.indexOfFirst { it != ' ' } .. col.indexOfLast { it != ' ' }
      }
      colIndices = colIndicesTmp
      currentPosition = Coordinate(0, rowIndices[0].first)
    }
  private var debugMap = List(0) { MutableList(0) { ' ' } }
  private var rowIndices = List(0) { 0 until 1 }
  private var colIndices = List(0) { 0 until 1 }
  private var currentPosition = Coordinate(0, 0)
  private var orientation = Orientation.RIGHT

  fun exists(coordinate: Coordinate): Boolean {
    return coordinate.x in rowIndices.indices && coordinate.y in colIndices.indices && coordinate.x in colIndices[coordinate.y] && coordinate.y in rowIndices[coordinate.x]
  }

  fun getFace(coordinate: Coordinate): Int {
    if (coordinate.x < cubeSize) {
      if (coordinate.y - rowIndices[coordinate.x].first in 0 until cubeSize) return 1
      return 2
    }
    if (coordinate.x < 2 * cubeSize) return 3
    if (coordinate.x < 3 * cubeSize) {
      if (coordinate.y - rowIndices[coordinate.x].first in 0 until cubeSize) return 4
      return 5
    }
    return 6
  }

  fun step(): Boolean {
    val next = when (orientation) {
      Orientation.RIGHT -> {
        if (currentPosition.y + 1 in rowIndices[currentPosition.x])
          Coordinate(currentPosition.x, currentPosition.y + 1)
        else
          Coordinate(currentPosition.x, rowIndices[currentPosition.x].first)
      }
      Orientation.DOWN -> {
        if (currentPosition.x + 1 in colIndices[currentPosition.y])
          Coordinate(currentPosition.x + 1, currentPosition.y)
        else
          Coordinate(colIndices[currentPosition.y].first, currentPosition.y)
      }
      Orientation.LEFT -> {
        if (currentPosition.y - 1 in rowIndices[currentPosition.x])
          Coordinate(currentPosition.x, currentPosition.y - 1)
        else
          Coordinate(currentPosition.x, rowIndices[currentPosition.x].last)
      }
      Orientation.UP -> {
        if (currentPosition.x - 1 in colIndices[currentPosition.y])
          Coordinate(currentPosition.x - 1, currentPosition.y)
        else
          Coordinate(colIndices[currentPosition.y].last, currentPosition.y)
      }
    }
    if (map[next.x][next.y] == '.') {
      currentPosition = next
      debugMap[next.x][next.y] = orientation.character
      return true
    }
    return false
  }

  fun stepCube(): Boolean {
    val next: Coordinate
    var orientation = this.orientation
    if (exists(Coordinate(currentPosition.x + this.orientation.dx, currentPosition.y + this.orientation.dy)))
      next = Coordinate(currentPosition.x + this.orientation.dx, currentPosition.y + this.orientation.dy)
    else {
      val link = this.orientation.nextFace(getFace(currentPosition))
      next = link.newCoordinate(orientation.getX(currentPosition))
      orientation = link.orientation
    }
    if (map[next.x][next.y] == '.') {
      currentPosition = next
      this.orientation = orientation
      debugMap[next.x][next.y] = orientation.character
      return true
    }
    return false
  }

  fun perform(instruction: String, cube: Boolean = false) {
    if (instruction[0].isDigit()) {
      repeat(instruction.toInt()) {
        if (!cube) {
          if (!step()) return@repeat
        } else {
          if (!stepCube()) return@repeat
        }
      }
    } else {
      orientation = if (instruction == "R") orientation.next else orientation.prev
      debugMap[currentPosition.x][currentPosition.y] = orientation.character
    }
  }

  fun getCode(): Int {
    return (currentPosition.x + 1) * 1000 + (currentPosition.y + 1) * 4 + orientation.code
  }

  override fun toString(): String {
    return debugMap.joinToString("\n") { it.joinToString("") }
  }
}

class InstructionIterator(val str: String) {
  private var position = 0

  fun isNotEmpty(): Boolean {
    return position != str.length
  }

  fun getNext(): String {
    if (position == str.length) return ""
    val start = position
    position++
    val isDigit = str[start].isDigit()
    while (position < str.length && str[position].isDigit() == isDigit) position++
    return str.substring(start, position)
  }
}

fun main() {
  fun parseInput(input: String, cubeSize: Int): Pair<MonkeyMap, InstructionIterator> {
    val (map, instructions) = input.split("\n\n")
    val monkeyMap = MonkeyMap(cubeSize)
    monkeyMap.map = map.split('\n')
    return monkeyMap to InstructionIterator(instructions.replace("\n", ""))
  }

  fun part1(input: String, cubeSize: Int): Int {
    val (monkeyMap, instructionIterator) = parseInput(input, cubeSize)
    while (instructionIterator.isNotEmpty()) {
      monkeyMap.perform(instructionIterator.getNext())
    }
    return monkeyMap.getCode()
  }

  fun part2(input: String, cubeSize: Int): Int {
    val (monkeyMap, instructionIterator) = parseInput(input, cubeSize)
    while (instructionIterator.isNotEmpty()) {
      monkeyMap.perform(instructionIterator.getNext(), true)
    }
    return monkeyMap.getCode()
  }

  val testInput = readInput("Day22_test")
  check(part1(testInput, 4) == 6032)

  val input = readInput(22)
  println(part1(input, 50))
  println(part2(input, 50))
}