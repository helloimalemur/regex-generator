package org.olafneumann.regex.generator.ui.model

import org.olafneumann.regex.generator.model.PatternRecognizerModel
import org.olafneumann.regex.generator.output.CodeGeneratorOptions
import org.olafneumann.regex.generator.regex.RecognizerMatchCombinerOptions
import kotlin.test.Test
import kotlin.test.assertEquals

class DisplayModelTest {
    @Test
    fun testRowDistribution() {
        val patternModel = PatternRecognizerModel(
            input = "asdasd",
            recognizerMatchCombinerOptions = RecognizerMatchCombinerOptions()
        )
        val displayModel = DisplayModel(
            showLoadingIndicator = true,
            showCookieBanner = true,
            showCopyButton = true,
            patternRecognizerModels = listOf(patternModel),
            modelPointer = 0,
            codeGeneratorOptions = CodeGeneratorOptions()
        )

        assertEquals(2, displayModel.rowsOfMatchPresenters.size, "Number of rows in display model")
        assertEquals(6, displayModel.rowsOfMatchPresenters[0].size, "Number of presenters in 1st row")
        assertEquals(1, displayModel.rowsOfMatchPresenters[1].size, "Number of presenters in 2nd row")
    }
}
