import kotlin.math.sign

const val MY_MONKEY_NAME = "humn"
const val ROOT = "root"

val monkeys = HashMap<String, Monkey>()

enum class Operation(val perform: (Double, Double) -> Double) {
  DIV(Double::div), MUL(Double::times), MINUS(Double::minus), PLUS(Double::plus);

  override fun toString(): String {
    return when (this) {
      DIV -> " / "
      MUL -> " * "
      MINUS -> " - "
      PLUS -> " + "
    }
  }

  companion object {
    fun getByString(op: String): Operation {
      return when (op) {
        "/" -> DIV
        "*" -> MUL
        "-" -> MINUS
        else -> PLUS
      }
    }
  }
}

abstract class Monkey {
  abstract fun get(): Double?
}

class OpMonkey(val left: String, val right: String, val op: Operation): Monkey() {
  var result: Double? = null
  var formula: ((Double) -> Double)? = null

  override fun get(): Double? {
    return result
  }

  override fun toString(): String {
    if (result != null) return result.toString()
    val builder = StringBuilder("(")
    builder.append(monkeys[left]!!)
    builder.append(op)
    builder.append(monkeys[right]!!)
    builder.append(")")
    return builder.toString()
  }
}

class NumMonkey(val number: Double, val isMe: Boolean = false): Monkey() {
  override fun get(): Double? {
    if (isMe) return null
    return number
  }

  override fun toString(): String {
    if (isMe) return "X"
    return number.toString()
  }
}

fun main() {
  fun parseInput(input: List<String>, countMyself: Boolean = false): Monkey {
    input.map {
      val words = it.replace(":", "").split(" ")
      val from = words[0]
      if (words.size == 4) monkeys[from] = OpMonkey(words[1], words[3], if (!countMyself || words[0] != ROOT) Operation.getByString(words[2]) else Operation.MINUS)
      else monkeys[from] = NumMonkey(words[1].toDouble(), countMyself && words[0] == MY_MONKEY_NAME)
    }
    return monkeys["root"]!!
  }

  fun countValue(current: Monkey) {
    if (current is NumMonkey || (current as OpMonkey).result != null) return
    val left = monkeys[current.left]!!
    countValue(left)
    val right = monkeys[current.right]!!
    countValue(right)
    if (left.get() == null && right.get() == null) {
      println("Logic failed...")
      return
    }
    if (left.get() == null) {
      if (left is NumMonkey) current.formula = { number -> current.op.perform(number, right.get()!!) }
      else current.formula = { number -> current.op.perform((left as OpMonkey).formula!!(number), right.get()!!) }
    } else if (right.get() == null) {
      if (right is NumMonkey) current.formula = { number -> current.op.perform(left.get()!!, number) }
      else current.formula = { number -> current.op.perform(left.get()!!, (right as OpMonkey).formula!!(number)) }
    } else {
      current.result = current.op.perform(left.get()!!, right.get()!!)
    }
  }

  fun part1(input: List<String>): Long {
    monkeys.clear()
    val root = parseInput(input)
    if (root is NumMonkey) return root.number.toLong()
    countValue(root)
    return (root as OpMonkey).result?.toLong() ?: 0L
  }

  fun findResult(root: OpMonkey): Long {
    fun apply(n: Long): Double = root.formula!!(n.toDouble())

    var left = 0L
    var right = 14564962656856
    val multiple = -apply(left).sign
    while (left < right - 1L) {
      val mid = (left + right) / 2L
      val midValue = apply(mid)
      if (midValue == 0.0) return mid
      else if (midValue * multiple < 0L) left = mid
      else right = mid
    }
    return left
  }

  fun part2(input: List<String>): Long {
    monkeys.clear()
    val root = parseInput(input, true)
    if (root is NumMonkey) return root.number.toLong()
    countValue(root)
    return findResult(root as OpMonkey)
  }

  val testInput = readInputLines("Day21_test")
  check(part1(testInput) == 152L)
  check(part2(testInput) == 301L)

  val input = readInputLines(21)
  println(part1(input))
  println(part2(input))
}