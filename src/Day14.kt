import java.lang.StringBuilder

enum class State {
    FINISHED, ADDED, BLOCKED;

    fun completed(): Boolean {
        return this != BLOCKED
    }
}

class Field() {
    private var snowCoordinate: Coordinate = Coordinate(500, 0)

    private var left: Int = 1000000
    private var right: Int = 0
    private var floor: Int = 0
    private val fieldMap = HashSet<Coordinate>()

    fun addStone(point: Coordinate): Boolean {
        fieldMap.add(point)
        left = left.coerceAtMost(point.x)
        right = right.coerceAtLeast(point.x)
        floor = floor.coerceAtLeast(point.y)
        return false
    }

    fun Coordinate.next() =
            listOf(Coordinate(this.x, this.y + 1), Coordinate(x - 1, y + 1), Coordinate(x + 1, y + 1))

    fun addSnow(coordinate: Coordinate = snowCoordinate, withFloor: Boolean = false): State {
        if (fieldMap.contains(coordinate)) return State.BLOCKED

        if (!withFloor && (coordinate.y > floor || coordinate.x !in left .. right))  return State.FINISHED
        if (withFloor && coordinate.y == floor + 2) return State.BLOCKED

        for (next in coordinate.next()) {
            val res = addSnow(next, withFloor)
            if (res.completed()) return res

        }
        fieldMap.add(coordinate)
        return State.ADDED
    }

    override fun toString(): String {
        val result = StringBuilder()
        for (y in 0 .. floor + 3) {
            for (x in fieldMap.toList().minOf { it.x } - 3 .. fieldMap.toList().maxOf { it.x } + 3) {
                result.append(if (fieldMap.contains(Coordinate(x, y))) "o" else ".")
            }
            result.append("\n")
        }
        return result.toString()
    }
}
fun main() {
    fun parseInput(input: List<String>): Field {
        val field = Field()
        input.forEach { line ->
            line
                .split(" -> ")
                .map { pair ->
                    val intPair = pair.split(",").map { it.toInt() }
                    Coordinate(intPair[0], intPair[1])
                }
                .windowed(2)
                .forEach { pair ->
                    for (x in pair[0].x.coerceAtMost(pair[1].x)..pair[0].x.coerceAtLeast(pair[1].x)) {
                        for (y in pair[0].y.coerceAtMost(pair[1].y)..pair[0].y.coerceAtLeast(pair[1].y)) {
                            field.addStone(Coordinate(x, y))
                        }
                    }
                }
        }
        return field
    }

    fun process(input: List<String>, withFloor: Boolean): Int {
        val field = parseInput(input)
        var counter = 0
        while (field.addSnow(withFloor = withFloor) == State.ADDED) counter++
        return counter
    }

    fun part1(input: List<String>): Int = process(input, false)
    fun part2(input: List<String>): Int = process(input, true)

    val testInput = readInputLines("Day14_test")
    check(part1(testInput) == 24)
    check(part2(testInput) == 93)

    val input = readInputLines(14)
    println(part1(input))
    println(part2(input))
}