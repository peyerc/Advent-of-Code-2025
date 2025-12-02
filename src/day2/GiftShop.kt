package day2

import java.io.File

private val idRanges = File("./src/day2/sample_input.txt")
    .readText()
    .replace("\n","")
    .split(",").map {
        val (start, end) = it.split("-").map { numStr -> numStr.toLong() }
        start..end
    }

fun main() {
    println("Day 2 - Gift Shop")

    val sum1 = idRanges.flatMap { idRange ->
        idRange.map { it.validate1() }
    }.sum()

    println("Part I: Total of invalid IDs is $sum1")

    val sum2 = idRanges.flatMap { idRange ->
        idRange.map { it.validate2() }
    }.sum()

    println("Part II: Total of invalid IDs is $sum2")
}

private fun Long.validate1(): Long {
    val stringRep = toString()
    (1 until stringRep.length).forEach {
        val substring = stringRep.take(it)
        val subStringTwice = substring.repeat(2)
        if (stringRep == subStringTwice) {
            return this
        }
    }
    return 0
}

private fun Long.validate2(): Long {
    val stringRep = toString()
    (1 until stringRep.length).forEach { length ->
        val substring = stringRep.take(length)
        (1 .. stringRep.length).forEach {
            val multipleSubstrings = substring.repeat(it+1)
            if (stringRep == multipleSubstrings) return this
        }
    }
    return 0
}
