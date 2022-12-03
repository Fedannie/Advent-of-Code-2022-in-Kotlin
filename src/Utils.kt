import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.math.BigInteger
import java.net.CookieHandler
import java.net.CookieManager
import java.net.HttpCookie
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.MessageDigest

const val SESSION_ID = "null"

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src/input", "$name.txt")
    .readLines()

/**
 * Reads all input lines from the advent of code input page.
 */
fun readInput(day: Int): List<String> {
    val url: URL? = try {
        URL("https://adventofcode.com/2022/day/${day}/input")
    } catch (e: Exception) {
        println("Failed to extract input")
        null
    }
    return url?.getLines() ?: ArrayList()
}

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

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