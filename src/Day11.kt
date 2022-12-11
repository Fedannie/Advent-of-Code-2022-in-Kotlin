fun main() {
  fun String.lastWord(): String = split(' ').last()

  class Algorithm(val divisibleBy: Int, val ifTrue: Int, val ifFalse: Int) {
    fun next(x: Long): Int = if (x % divisibleBy == 0L) ifTrue else ifFalse
  }

  class Monkey(items: Collection<Long>, val operation: (Long) -> Long, val algorithm: Algorithm) {
    val items = MutableList(0) { 0L }
    var inspected = 0L
      private set
    var postprocess: (Long) -> Long = { x -> x }

    init {
      this.items.addAll(items)
    }

    fun processItems(monkeys: List<Monkey>) {
      for (item in items) {
        val current = postprocess(operation(item))
        monkeys[algorithm.next(current)].items.add(current)
        inspected++
      }
      items.clear()
    }
  }

  fun parseOperation(operationStr: String): (Long) -> Long {
    if (operationStr == "old * old") return { x -> x * x }
    if (operationStr == "old + old") return { x -> 2 * x }
    val words = operationStr.split(' ')
    val right = words[2].toLong()
    when (words[1]) {
      "*" -> return { x -> x * right }
      else -> return { x -> x + right }
    }
  }

  fun parseInput(input: String): List<Monkey> {
    return input.split("\n\n").map {
      it.split("\n").subList(1, 6)
    }.map {
      val items = it[0].substring("  Starting items: ".length).split(", ").map { it.toLong() }
      val operation = parseOperation(it[1].substring("  Operation: new = ".length))
      val algorithm = Algorithm(it[2].lastWord().toInt(), it[3].lastWord().toInt(), it[4].lastWord().toInt())
      Monkey(items, operation, algorithm)
    }
  }

  fun monkeyBusiness(monkeys: List<Monkey>): Long =
    monkeys.map { it.inspected }.sortedDescending().subList(0, 2).reduceRight { a, b -> a * b }

  fun part1(input: String): Long {
    val monkeys = parseInput(input)
    monkeys.forEach { it.postprocess = { x -> x / 3 } }
    repeat (20) {
      for (monkey in monkeys) {
        monkey.processItems(monkeys)
      }
    }
    return monkeyBusiness(monkeys)
  }

  fun part2(input: String): Long {
    val monkeys = parseInput(input)
    val bigNumber = monkeys.fold(1) { current, monkey -> current * monkey.algorithm.divisibleBy }
    monkeys.forEach { it.postprocess = { x -> x % bigNumber } }
    repeat (10000) {
      for (monkey in monkeys) {
        monkey.processItems(monkeys)
      }
    }
    return monkeyBusiness(monkeys)
  }

  val testInput = readInput("Day11_test")
  check(part1(testInput) == 10605L)
  check(part2(testInput) == 2713310158)

  val input = readInput(11)
  println(part1(input))
  println(part2(input))
}