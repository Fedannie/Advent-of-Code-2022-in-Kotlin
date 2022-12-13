fun main() {
  open class Node

  class NumberNode(val value: Int): Node() {
    override fun toString(): String {
      return value.toString()
    }
  }

  class ListNode(): Node() {
    val values = MutableList(0) { Node() }

    constructor(items: List<Node>): this() {
      values.addAll(items)
    }

    override fun toString(): String {
      return "[${values.joinToString(",")}]"
    }
  }

  operator fun Node.compareTo(other: Node): Int {
    return when (this) {
      is NumberNode -> {
        when (other) {
          is NumberNode -> this.value.compareTo(other.value)
          else -> ListNode(listOf(this)).compareTo(other)
        }
      }
      is ListNode -> {
        when (other) {
          is NumberNode -> this.compareTo(ListNode(listOf(other)))
          is ListNode -> (this.values zip other.values)
            .map { it.first.compareTo(it.second) }
            .find { it != 0 } ?:
            this.values.size.compareTo(other.values.size)
          else -> 0
        }
      }
      else -> 0
    }
  }

  fun parseNode(line: List<String>, cur: Int): Pair<Node, Int> {
    if (line[cur] == "[") {
      var nextInd = cur + 1
      val node = ListNode()
      while (line[nextInd] != "]") {
        val result = parseNode(line, nextInd)
        node.values.add(result.first)
        nextInd = result.second
      }
      return node to nextInd + 1
    }
    return NumberNode(line[cur].toInt()) to cur + 1
  }

  fun parseNode(line: String): Node {
    val words = line
      .replace("[", ",[,")
      .replace("]", ",],")
      .removeSurrounding(",")
      .split(",")
      .filter { it.isNotEmpty() }
    return parseNode(words, 0).first
  }

  fun parseInput(input: List<String>): List<Pair<Node, Node>> {
    return input.filter { it.isNotEmpty() }.chunked(2).map { parseNode(it[0]) to parseNode(it[1]) }
  }

  fun part1(input: List<String>): Int {
    val pairs = parseInput(input)
    return pairs
      .mapIndexed { i, pair -> i + 1 to pair.first.compareTo(pair.second) }
      .filter { it.second < 0 }
      .sumOf { it.first }
  }

  fun part2(input: List<String>): Int {
    val packets = parseInput(input).flatMap { listOf(it.first, it.second) }.toMutableList()
    val divider1 = ListNode(listOf(ListNode(listOf(NumberNode(2)))))
    val divider2 = ListNode(listOf(ListNode(listOf(NumberNode(6)))))
    packets.add(divider1)
    packets.add(divider2)
    val sorted = packets.sortedWith { a, b -> a.compareTo(b) }
    return (sorted.indexOf(divider1) + 1) * (sorted.indexOf(divider2) + 1)
  }

  val testInput = readInputLines("Day13_test")
  check(part1(testInput) == 13)
  check(part2(testInput) == 140)

  val input = readInputLines(13)
  println(part1(input))
  println(part2(input))
}