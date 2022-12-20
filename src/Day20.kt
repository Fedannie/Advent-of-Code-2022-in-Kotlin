const val DECRYPTION_KEY = 811589153L

fun main() {
  fun List<Long>.grove(): Long {
    return listOf(1000, 2000, 3000).sumOf { this[(indexOf(0) + it) % size] }
  }

  fun parseInput(input: List<String>, key: Long = 1): List<Long> {
    return input.map { it.toLong() * key }
  }

  fun mix(order: List<Long>, values: List<Pair<Int, Long>> = order.mapIndexed { i, v -> i to v }): List<Pair<Int, Long>> {
    val mixed = values.toMutableList()
    for (i in values.indices) {
      val index = mixed.indexOfFirst { it.first == i }
      val next = mixed[index]
      mixed.removeAt(index)
      val newPosition = (index + next.second).mod(values.size - 1)
      mixed.add(if (newPosition == 0) values.size - 1 else newPosition, next)
    }
    return mixed
  }

  fun part1(input: List<String>): Long {
    val initial = parseInput(input)
    return mix(initial).map { it.second }.grove()
  }


  fun part2(input: List<String>): Long {
    val order = parseInput(input, DECRYPTION_KEY)
    var values = order.mapIndexed { i, v -> i to v }
    repeat(10) {
      values = mix(order, values)
    }
    return values.map { it.second }.grove()
  }

  val testInput = readInputLines("Day20_test")
  check(part1(testInput) == 3L)
  check(part2(testInput) == 1623178306L)

  val input = readInputLines(20)
  println(part1(input))
  println(part2(input))
}