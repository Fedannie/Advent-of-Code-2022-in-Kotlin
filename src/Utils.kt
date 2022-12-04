import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.net.CookieHandler
import java.net.CookieManager
import java.net.HttpCookie
import java.net.URL

const val SESSION_ID = "null"

/**
 * Reads lines from the given input txt file.
 */
fun readInputLines(name: String) = File("src/input", "$name.txt").readLines()

/**
 * Reads from the given input txt file.
 */
fun readInput(name: String) = File("src/input", "$name.txt").readText()

/**
 * Reads all input lines from the advent of code input page.
 */
fun readInputLines(day: Int): List<String> {
    val url: URL? = try {
        URL("https://adventofcode.com/2022/day/${day}/input")
    } catch (e: Exception) {
        println("Failed to extract input")
        null
    }
    return url?.getLines() ?: ArrayList()
}

fun readInput(day: Int): String = readInputLines(day).joinToString("\n")

fun URL.getLines(): List<String> {
    return try {
        val sessionCookie = HttpCookie("session", SESSION_ID)
        sessionCookie.path = "/"
        sessionCookie.version = 0

        val cookieManager = CookieManager()
        CookieHandler.setDefault(cookieManager)
        cookieManager.cookieStore.add(toURI(), sessionCookie)

        BufferedReader(InputStreamReader(openStream())).readLines()
    } catch (_: IOException) {
        ArrayList()
    }
}