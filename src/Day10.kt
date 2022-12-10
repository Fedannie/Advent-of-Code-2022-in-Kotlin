import kotlin.math.abs

const val LAST_ROW = 240
const val LENGTH = 40

fun main() {
  class Clock {
    private var x = 1
    private var time = 0
    private var onTick: (Int, Int) -> Unit = { a, b -> }

    fun parseAction(action: String): Pair<Int, Int> {
      val words = action.split(" ")
      if (words[0] == "noop") return 1 to 0
      return 2 to words[1].toInt()
    }

    fun perform(action: String) {
      val (ticks, dx) = parseAction(action)
      repeat (ticks) {
        time++
        onTick(time, x)
      }
      x += dx
    }

    fun setOnTickListener(onTick: (Int, Int) -> Unit) {
      this.onTick = onTick
    }
  }

  fun part1(input: List<String>): Int {
    val clock = Clock()
    var signalStrength = 0
    clock.setOnTickListener { time, x ->
      if (time % LENGTH == 20 && time <= LAST_ROW)
        signalStrength += x * time
    }
    input.forEach { clock.perform(it) }
    return signalStrength
  }

  fun part2(input: List<String>): String {
    val clock = Clock()
    val field = Array(LAST_ROW / LENGTH) { Array(LENGTH) { "." } }
    clock.setOnTickListener { time, x ->
      if (abs((time - 1) % LENGTH - x) <= 1)
        field[(time - 1) / LENGTH][(time - 1) % LENGTH] = "#"
    }
    input.forEach { clock.perform(it) }
    return field.joinToString("\n") { it.joinToString("") }
  }

  val testInput = readInputLines("Day10_test")
  check(part1(testInput) == 13140)
  check(part2(testInput) == """##..##..##..##..##..##..##..##..##..##..
###...###...###...###...###...###...###.
####....####....####....####....####....
#####.....#####.....#####.....#####.....
######......######......######......####
#######.......#######.......#######.....""")

  val input = readInputLines(10)
  println(part1(input))
  println(part2(input))
}