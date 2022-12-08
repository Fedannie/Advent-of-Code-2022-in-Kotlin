fun main() {
  fun parseInput(input: List<String>): List<List<Int>> = input.map { it.toCharArray().toList().map { it.toString().toInt() } }

  fun List<List<Int>>.up(i: Int, j: Int) = map { it[j] }.subList(0, i)
  fun List<List<Int>>.down(i: Int, j: Int) = map { it[j] }.subList(i + 1, size)
  fun List<List<Int>>.left(i: Int, j: Int) = this[i].subList(0, j)
  fun List<List<Int>>.right(i: Int, j: Int) = this[i].subList(j + 1, this[i].size)

  fun countNumber(i: Int, j: Int, field: List<List<Int>>): Int {
    fun criterion(value: Int): Boolean = value >= field[i][j]

    val left = j - 0.coerceAtLeast(field.left(i, j).indexOfLast(::criterion))
    var right = field.right(i, j).indexOfFirst(::criterion) + 1
    if (right == 0) right = field[i].size - j - 1

    val up = i - 0.coerceAtLeast(field.up(i, j).indexOfLast(::criterion))
    var down = field.down(i, j).indexOfFirst(::criterion) + 1
    if (down == 0) down = field.size - i - 1

    return left * right * up * down
  }

  fun isVisible(i: Int, j: Int, field: List<List<Int>>): Boolean {
    fun criterion(value: Int): Boolean = value < field[i][j]

    return field.up(i, j).all(::criterion) || field.down(i, j).all(::criterion) ||
        field.left(i, j).all(::criterion) || field.right(i, j).all(::criterion)
  }

  fun part1(input: List<String>): Int {
    val field = parseInput(input)
    return field.mapIndexed { i, row -> row.filterIndexed { j, _ -> isVisible(i, j, field) }.count() }.sum()
  }

  fun part2(input: List<String>): Int {
    val field = parseInput(input)
    return field.mapIndexed { i, row -> List(row.size) { j -> countNumber(i, j, field) } }.maxOf { it.max() }
  }

  val testInput = readInputLines("Day08_test")
  check(part1(testInput) == 21)
  check(part2(testInput) == 8)

  val input = readInputLines(8)
  println(part1(input))
  println(part2(input))
}