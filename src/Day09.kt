import kotlin.math.abs
import kotlin.math.sign

const val FIELD_SIZE = 610

fun main() {
  fun parseInput(input: List<String>): List<Pair<Int, Int>> {
    return input.map {
      val (direction, stepsStr) = it.split(' ')
      val steps = stepsStr.toInt()
      when (direction) {
        "D" -> steps to 0
        "U" -> -steps to 0
        "L" -> 0 to -steps
        "R" -> 0 to steps
        else -> 0 to 0
      }
    }
  }

  class Field(size: Int, length: Int) {
    private val field = Array(size) { BooleanArray(size) { false } }
    val rope = Array(length) { size / 2 to size / 2 }
    val visited: Int
      get() {
        return this.field.sumOf { it.count { it }}
      }

    fun dist(pointA: Pair<Int, Int>, pointB: Pair<Int, Int>): Int {
      return abs(pointA.first - pointB.first).coerceAtLeast(abs(pointA.second - pointB.second))
    }

    fun performAction(dy: Int, dx: Int) {
      for (y in 1 .. abs(dy).coerceAtLeast(abs(dx))) {
        rope[0] = rope[0].first + dy.sign to rope[0].second + dx.sign
        for (i in 1 until rope.size) {
          if (dist(rope[i], rope[i - 1]) > 1) {
            rope[i] = rope[i].first + (rope[i - 1].first - rope[i].first).sign to rope[i].second + (rope[i - 1].second - rope[i].second).sign
          }
        }
        field[rope.last().first][rope.last().second] = true
      }
    }
  }

  fun run(input: List<String>, ropeLength: Int): Int {
    val directions = parseInput(input)
    val field = Field(FIELD_SIZE, ropeLength)

    directions.forEach { field.performAction(it.first, it.second) }
    return field.visited
  }

  fun part1(input: List<String>): Int = run(input, 2)

  fun part2(input: List<String>): Int = run(input, 10)

  val testInput = readInputLines("Day09_test")
  check(part1(testInput) == 13)
  check(part2(testInput) == 1)

  val input = readInputLines(9)
  println(part1(input))
  println(part2(input))
}