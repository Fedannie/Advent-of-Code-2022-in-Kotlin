import kotlin.math.max
import kotlin.math.min

class MineralsState(
  val time: Int,
  val minerals: MineralsCount,
  val robots: MineralsCount,
  val priceList: PriceList
  ) {
  private val deltaRobots = arrayOf(MineralsCount(1, 0, 0, 0), MineralsCount(0, 1, 0, 0), MineralsCount(0, 0, 1, 0), MineralsCount(0, 0, 0, 1))
  val next: List<MineralsState>
    get() {
      val result = MutableList(0) { this }
      if (time == 0) return result
      for (i in 3 downTo 3) {
        if (minerals >= priceList.get(i)) {
          result.add(
            MineralsState(
              time - 1,
              minerals + robots - priceList.get(i),
              robots + deltaRobots[i],
              priceList
            )
          )
          return result
        }
      }

      for (i in 2 downTo  0) {
        if (minerals >= priceList.get(i) && robots.get(i) < priceList.max(i) && minerals.get(i) < (time + 1) * priceList.max(i) && minerals.get(i) < 100) {
          result.add(
            MineralsState(
              time - 1,
              minerals + robots - priceList.get(i),
              robots + deltaRobots[i],
              priceList
            )
          )
        }
      }
      result.add(
        MineralsState(
          time - 1,
          minerals + robots,
          robots,
          priceList
        )
      )
      return result
    }

  override fun hashCode(): Int {
    return (time to (minerals to robots)).hashCode()
  }

  override fun toString(): String {
    return "At $time: ore-robots=${robots.ore}, clay-robots=${robots.clay}, obsidian-robots=${robots.obsidian}, geode-robots=${robots.geode}, {ore=${minerals.ore}, clay=${minerals.clay}, obsidian=${minerals.obsidian}, geode=${minerals.geode}}"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as MineralsState

    if (time != other.time) return false
    if (minerals != other.minerals) return false
    if (robots != other.robots) return false

    return true
  }
}

class MineralsCount(val ore: Int, val clay: Int, val obsidian: Int, val geode: Int) {
  override fun hashCode(): Int {
    return ((ore to clay) to (obsidian to geode)).hashCode()
  }

  override fun toString(): String {
    return "costs $ore ore" + (if (clay > 0) " and $clay clay" else "") + (if (obsidian > 0) " and $obsidian obsidian" else "")
  }

  operator fun plus(other: MineralsCount): MineralsCount {
    return MineralsCount(ore + other.ore, clay + other.clay, obsidian + other.obsidian, geode + other.geode)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as MineralsCount

    if (ore != other.ore) return false
    if (clay != other.clay) return false
    if (obsidian != other.obsidian) return false
    if (geode != other.geode) return false

    return true
  }

  operator fun compareTo(other: MineralsCount): Int {
    return min(min(ore.compareTo(other.ore), clay.compareTo(other.clay)), min(obsidian.compareTo(other.obsidian), geode.compareTo(other.geode)))
  }

  operator fun minus(other: MineralsCount): MineralsCount {
    return MineralsCount(ore - other.ore, clay - other.clay, obsidian - other.obsidian, geode - other.geode)
  }

  fun get(i: Int): Int {
    return when (i) {
      0 -> ore
      1 -> clay
      2 -> obsidian
      else -> geode
    }
  }
}

class PriceList(val ore: MineralsCount, val clay: MineralsCount, val obsidian: MineralsCount, val geode: MineralsCount) {
  fun get(i: Int): MineralsCount {
    return when (i) {
      0 -> ore
      1 -> clay
      2 -> obsidian
      else -> geode
    }
  }

  override fun toString(): String {
    return "Ore-robot $ore\nClay-robot $clay\nObsidian-robot $obsidian\nGeode-Robot $geode."
  }

  fun max(i: Int): Int {
    return max(max(ore.get(i), clay.get(i)), max(obsidian.get(i), geode.get(i)))
  }
}

fun main() {
  fun parseInput(input: List<String>): List<PriceList> {
    return input.map { it.replace(":", ".").split(".").subList(1, 5) }.map {
      it.map {
        val regex = """ Each [\w]+ robot costs (?<ore>\d+) ore( and (?<clay>\d+) clay)?( and (?<obsidian>\d+) obsidian)?""".toRegex()
        val matchResult = regex.find(it)!!.groups as? MatchNamedGroupCollection
        MineralsCount(
          matchResult?.get("ore")?.value?.toInt() ?: 0,
          matchResult?.get("clay")?.value?.toInt() ?: 0,
          matchResult?.get("obsidian")?.value?.toInt() ?: 0,
          0
        )
      }
    }.map {
      PriceList(it[0], it[1], it[2], it[3])
    }
  }

  fun countGeodes(priceList: PriceList, time: Int): Int {
    val visited = HashMap<Pair<Int, MineralsCount>, MineralsCount>()
    val queue = MutableList(1) { MineralsState(time - 1, MineralsCount(1, 0, 0, 0), MineralsCount(1, 0, 0, 0), priceList) }
    var result = 0
    while (queue.isNotEmpty()) {
      val current = queue.removeFirst()
      val state = current.time to current.robots
      if (visited.contains(state) && visited[state]!! >= current.minerals) continue
      result = max(result, current.minerals.geode)
      visited[state] = current.minerals
      queue.addAll(current.next)
    }
    return result
  }

  fun part1(input: List<String>): Int {
    val blueprints = parseInput(input)
    return blueprints.mapIndexed { i, priceList -> (i + 1) * countGeodes(priceList, 24) }.sum()
  }

  fun part2(input: List<String>): Int {
    val blueprints = parseInput(input)
    return blueprints.subList(0, min(3, blueprints.size)).map { priceList -> countGeodes(priceList, 32) }.fold(1, Int::times)
  }

  val testInput = readInputLines("Day19_test")
  check(part1(testInput) == 33)
  check(part2(testInput) == 62 * 56)

  val input = readInputLines(19)
  println(part1(input))
  println(part2(input))
}