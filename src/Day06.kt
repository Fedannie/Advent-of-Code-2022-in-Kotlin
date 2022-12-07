fun main() {
  fun countMarker(input: String, size: Int): Int = input.windowed(size).indexOfFirst { it.toSet().size == size } + size

  fun part1(input: String): Int = countMarker(input, 4)

  fun part2(input: String): Int = countMarker(input, 14)

  val testInput = readInputLines("Day06_test")[0]
  check(part1(testInput) == 7)
  check(part2(testInput) == 19)

  val input = readInputLines(6)[0]
  println(part1(input))
  println(part2(input))
}