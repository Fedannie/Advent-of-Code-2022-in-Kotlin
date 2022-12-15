import kotlin.concurrent.fixedRateTimer
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
  fun parseInput(input: List<String>): List<List<Coordinate>> {
    return input.map {
      it
        .replace("Sensor at x=", "")
        .replace(" y=", "")
        .replace(": closest beacon is at x=", ",")
        .split(",")
        .map { it.toInt() }
        .chunked(2)
        .map { Coordinate(it[0], it[1]) }
    }
  }

  fun unify(segments: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
    val result = MutableList(0) { 0 to 0 }
    var current = segments[0]
    for (i in segments.indices) {
      if (segments[i].first <= current.second) {
        if (segments[i].second > current.second) {
          current = current.first to segments[i].second
        }
      } else {
        result.add(current)
        current = segments[i]
      }
    }
    result.add(current)
    return result
  }

  fun getLine(pairs: List<List<Coordinate>>, y: Int, left: Int = -50000000, right: Int = 50000000): List<Pair<Int, Int>> {
    val field = MutableList(0) { 0 to 0 }
    pairs.forEach { (sensor, beacon) ->
      val dx = sensor - beacon - abs(sensor.y - y)
      if (dx >= 0 && sensor.x + dx >= left && sensor.x - dx <= right) {
        field.add(max(left, sensor.x - dx) to min(sensor.x + dx, right))
      }
    }
    return unify(field.sortedWith(compareBy<Pair<Int, Int>> { it.first }.thenBy { it.second }))
  }

  fun part1(input: List<String>, y: Int): Int {
    val pairs = parseInput(input)
    val coveredSegments = getLine(pairs, y)
    val covered = coveredSegments.map { (it.first .. it.second).toSet() }.reduce { setA, setB -> setA.union(setB) }
    return covered.subtract(pairs.filter { it[1].y == y }.map { it[1].x }.toSet()).size
  }

  fun part2(input: List<String>, border: Int): Long {
    val pairs = parseInput(input)
    for (y in 0 .. border) {
      val covered = getLine(pairs, y, 0, border)
      if (covered.size == 2) {
        return y.toLong() + (covered[0].second + 1).toLong() * 4000000L
      }
    }

    return 0
  }

  val testInput = readInputLines("Day15_test")
  check(part1(testInput, 10) == 26)
  check(part2(testInput, 20) == 56000011L)

  val input = readInputLines(15)
  println(part1(input, 2000000))
  println(part2(input, 4000000))
}