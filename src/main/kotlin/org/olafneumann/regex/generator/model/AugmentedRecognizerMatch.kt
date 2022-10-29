package org.olafneumann.regex.generator.model

import dev.andrewbailey.diff.DiffOperation
import org.olafneumann.regex.generator.RegexGeneratorException
import org.olafneumann.regex.generator.model.AugmentedRecognizerMatch.Companion.RangeAction.Companion.add
import org.olafneumann.regex.generator.model.AugmentedRecognizerMatch.Companion.RangeAction.Companion.remove
import org.olafneumann.regex.generator.regex.RecognizerMatch
import org.olafneumann.regex.generator.util.HasRange
import org.olafneumann.regex.generator.util.HasRanges
import org.olafneumann.regex.generator.util.add
import org.olafneumann.regex.generator.util.remove

class AugmentedRecognizerMatch(
    val original: RecognizerMatch,
    override val ranges: List<IntRange> = original.ranges
) : HasRange, HasRanges {
    override val first: Int
        get() = this.ranges[0].first
    override val last: Int
        get() = this.ranges.last().last


    fun <T> applyAll(diffOperations: List<DiffOperation<T>>?): AugmentedRecognizerMatch? {
        var out: AugmentedRecognizerMatch? = this
        diffOperations?.forEach { out = out?.apply(it) }
        return out
    }

    private val <T> DiffOperation<T>.rangeAction: RangeAction get() =
        when (this) {
            is DiffOperation.Add -> RangeAction(add, IntRange(this.index, this.index))
            is DiffOperation.AddAll -> RangeAction(add, IntRange(this.index, this.index + this.items.size - 1))
            is DiffOperation.Remove -> RangeAction(remove, IntRange(this.index, this.index))
            is DiffOperation.RemoveRange -> RangeAction(remove, IntRange(this.startIndex, this.endIndex))
            else -> throw RegexGeneratorException("Unknown DiffOperation: $this")
        }

    companion object {
        private data class RangeAction(
            val action: (IntRange, IntRange) -> IntRange?,
            val range: IntRange
        ) {
            fun applyTo(range: IntRange): IntRange? = action(range, this.range)

            companion object {
                val add: (IntRange, IntRange) -> IntRange? = { a, b -> a.add(b) }
                val remove: (IntRange, IntRange) -> IntRange? = { a, b -> a.remove(b) }
            }
        }
    }

    private fun <T> apply(diffOperation: DiffOperation<T>): AugmentedRecognizerMatch? {
        val rangeAction = diffOperation.rangeAction
        val newRanges = ranges.mapNotNull { rangeAction.applyTo(it) }

        return if (newRanges.size == ranges.size) {
            AugmentedRecognizerMatch(original = original, ranges = newRanges)
        } else {
            null
        }
    }

    override fun equals(other: Any?): Boolean {
        val out = when (other) {
            null -> false
            is AugmentedRecognizerMatch ->
                original.recognizer == other.original.recognizer
                        && original.title == other.original.title
                        && original.patterns == other.original.patterns
                        && this.hasSameRangesAs(other)

            is RecognizerMatch ->
                original.recognizer == other.recognizer
                        && original.title == other.title
                        && original.patterns == other.patterns
                        && this.hasSameRangesAs(other)

            else -> false
        }
        console.log(out.toString(), toString(), "Equals", other)
        return out
    }


    override fun hashCode(): Int {
        var result = original.recognizer.hashCode()
        result = 31 * result + original.patterns.hashCode()
        result = 31 * result + original.title.hashCode()
        result = 31 * result + ranges.hashCode()
        return result
    }

    override fun toString(): String {
        return "AugmentedRecognizerMatch(title=${original.title}, position=$first/$length, priority=${original.priority}, recognizer=${original.recognizer.name}, patterns=${original.patterns})"
    }
}
