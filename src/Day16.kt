import java.util.PriorityQueue
import kotlin.math.max

class Valve(val name: String, val flowRate: Int, val index: Int) {
  val next = MutableList(0) { "" to 1 }
}

const val startValve = "AA"

class DFSState(val last: Valve, val time: Int, val open: Set<String>): Comparable<DFSState> {
  override fun compareTo(other: DFSState): Int {
    return if (time == other.time) -(open.size.compareTo(other.open.size)) else (time.compareTo(other.time))
  }

  override fun toString(): String {
    return "{At ${last.name}, time=$time, open=[${open.toList()}]}"
  }
}

fun main() {
  val indicesByName = HashMap<String, Int>()
  val valves = MutableList<Valve>(0) { Valve("", 0, 0) }
  val nonNullValves = HashSet<String>()

  fun flatten() {
    for (valve in valves) {
      if (valve.next.size == 2 && valve.flowRate == 0) {
        val left = indicesByName[valve.next[0].first]!!
        val right = indicesByName[valve.next[1].first]!!
        val dist = valve.next.sumOf { it.second }
        valves[left].next.add(valve.next[1].first to dist)
        valves[right].next.add(valve.next[0].first to dist)
        valves[left].next.removeIf { it.first == valve.name }
        valves[right].next.removeIf { it.first == valve.name }
      }
    }
    for (valve in valves) valve.next.sortByDescending { valves[indicesByName[it.first]!!].flowRate }
  }

  fun <T: Comparable<T>> Set<T>.joinAll(): String {
    return toList().sorted().joinToString("")
  }

  fun parseInput(input: List<String>): Valve {
    input.map {
      it
        .replace("Valve ", "")
        .replace(" has flow rate=", ", ")
        .replace("; tunnels lead to valves", ",")
        .replace("; tunnel leads to valve", ",")
        .split(", ")
    }.forEachIndexed { index, line ->
      valves.add(Valve(line[0], line[1].toInt(), index))
      valves[index].next.addAll(line.subList(2, line.size).map { it to 1 })
      indicesByName[line[0]] = index
    }
    flatten()
    for (valve in valves) {
      if (valve.flowRate != 0) nonNullValves.add(valve.name)
    }
    return valves[indicesByName[startValve]!!]
  }

  fun <T> Set<T>.addAndClone(element: T?): Set<T> {
    val result = HashSet<T>(this)
    if (element != null) result.add(element)
    return result
  }

  val distances = Array(70) { IntArray(70) { 31 } }

  val visited = HashMap<Pair<Int, Pair<String, String>>, Int>()

  fun dfs(state: DFSState, allowedValves: Set<String>, maxTime: Int = 30): Int {
    if (state.time >= maxTime || state.open.size == allowedValves.size) {
      return 0
    }
    val stateDfs = state.time to (state.last.name to state.open.joinAll())
    if (visited.contains(stateDfs)) return visited[stateDfs]!!
    var result = 0
    for (valve in allowedValves.subtract(state.open)) {
      val newTime = state.time + distances[state.last.index][valves[indicesByName[valve]!!].index] + 1
      val newNode = valves[indicesByName[valve]!!]
      result = max(result, dfs(DFSState(newNode, newTime, state.open.addAndClone(newNode.name)), allowedValves, maxTime) + newNode.flowRate * (maxTime - newTime))
    }
    visited[stateDfs] = result
    return result
  }

  fun calculateDistances() {
    for (i in valves.indices) {
      val queue = PriorityQueue<Pair<Int, Int>>(valves.size, compareBy { it.first })
      queue.add(i to 0)
      while (queue.isNotEmpty()) {
        val current = queue.poll()
        if (current.second >= distances[i][current.first]) continue
        distances[i][current.first] = current.second
        for (nextValve in valves[current.first].next) {
          queue.add(indicesByName[nextValve.first]!! to current.second + nextValve.second)
        }
      }
    }
  }

  fun generateSubsets(from: List<String>): List<Set<String>> {
    val subsets = MutableList(0) { HashSet<String>() }
    for (b in 0 until (1 shl from.size)) {
      val subset = HashSet<String>()
      for (i in from.indices) {
        if ((b and (1 shl i)) != 0) {
          subset.add(from[i])
        }
      }
      if (subset.size in (from.size / 2 - 5) .. (from.size / 2 + 5))
        subsets.add(subset)
    }
    return subsets
  }

  fun part1(input: List<String>): Int {
    visited.clear()
    val graph = parseInput(input)
    calculateDistances()
    val result = dfs(DFSState(graph, 0, HashSet()), nonNullValves, 30)
    return result
  }

  fun part2(input: List<String>): Int {
    val graph = parseInput(input)
    calculateDistances()
    var result = 0
    for (split in generateSubsets(nonNullValves.toList())) {
      visited.clear()
      val result1 = dfs(DFSState(graph, 0, HashSet()), split, 26)
      visited.clear()
      val result2 = dfs(DFSState(graph, 0, HashSet()), nonNullValves.subtract(split), 26)
      if (result1 + result2 > result) {
        result =  result1 + result2
        println(result)
      }
    }
    return result
  }

  val testInput = readInputLines("Day16_test")
  check(part1(testInput) == 1651)
  check(part2(testInput) == 1707)

  val input = readInputLines(16)
  println(part1(input))
  println(part2(input))
}