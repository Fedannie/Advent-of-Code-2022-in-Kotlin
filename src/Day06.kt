fun main() {
  fun MutableMap<Char, Int>.dec(ch: Char) {
    if ((this[ch] ?: 1) == 1) remove(ch)
    else this[ch] = this[ch]!! - 1
  }

  fun MutableMap<Char, Int>.inc(ch: Char) {
    this[ch] = (this[ch] ?: 0) + 1
  }

  fun countMarker(input: String, size: Int): Int {
    val memory = input.subSequence(0, size).groupingBy { it }.eachCount().toMutableMap()
    for (i in size until input.length) {
      memory.dec(input[i - size])
      memory.inc(input[i])
      if (memory.size == size) return i + 1
    }
    return -1
  }

  fun part1(input: String): Int = countMarker(input, 4)

  fun part2(input: String): Int = countMarker(input, 14)

  val testInput = readInputLines("Day06_test")[0]
  check(part1(testInput) == 7)
  check(part2(testInput) == 19)

  val input = readInputLines(6)[0]
  println(part1(input))
  println(part2(input))
}