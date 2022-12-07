fun main() {
  val MAX_SIZE = 100000
  val TOTAL_SIZE = 70000000
  val FREE_SPACE = 30000000

  fun String.isCommand(): Boolean = startsWith("$ ")
  fun String.isLs(): Boolean = startsWith("$ ls")

  open class File(val name: String, val parent: File?, var size: Int = 0, val isDir: Boolean = false) {
    private val files = ArrayList<File>(1)

    val root: File
      get() {
        return parent?.root ?: parent ?: this
      }

    fun addFile(file: File) {
      files.add(file)

      var current: File? = this
      while (current != null) {
        current.size += file.size
        current = current.parent
      }
    }

    fun navigate(name: String): File {
      if (name == "..") return parent ?: this
      if (name == "/") return root
      return files.first { it.name == name }
    }

    fun filter(condition: (File) -> Boolean): List<File> {
      val result = MutableList(0) { File("", null) }
      if (condition(this)) result.add(this)
      if (isDir) files.forEach { result.addAll(it.filter(condition)) }
      return result
    }
  }

  fun parseInput(input: List<String>): File {
    var current = File("/", null, isDir = true)

    input.forEach { line ->
      if (line.isLs()) return@forEach
      val words = line.split(' ')
      if (line.isCommand()) current = current.navigate(words.last())
      else {
        if (words[0] == "dir") current.addFile(File(words[1], current, isDir = true))
        else current.addFile(File(words[1], current, words[0].toInt()))
      }

    }

    return current.root
  }


  fun part1(input: List<String>): Int = parseInput(input).filter { file -> file.isDir && file.size < MAX_SIZE }.sumOf { it.size }

  fun part2(input: List<String>): Int {
    val tree = parseInput(input)
    val needToFree = FREE_SPACE - (TOTAL_SIZE - tree.size)
    return parseInput(input).filter { file -> file.isDir && file.size >= needToFree }.minOf { it.size }
  }

  val testInput = readInputLines("Day07_test")
  check(part1(testInput) == 95437)
  check(part2(testInput) == 24933642)

  val input = readInputLines(7)
  println(part1(input))
  println(part2(input))
}