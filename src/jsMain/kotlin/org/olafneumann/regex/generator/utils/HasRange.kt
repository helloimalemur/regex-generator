package org.olafneumann.regex.generator.utils

interface HasRange {
    val first: Int
    val last: Int
    val length: Int
        get() = last - first + 1
    val range: IntRange
        get() = IntRange(first, last)

    companion object {
        private val byIndex = compareBy<HasRange> { it.first }
        private val byLength = compareByDescending<HasRange> { it.length }
        val byPosition = byIndex.then(byLength)
    }
}
