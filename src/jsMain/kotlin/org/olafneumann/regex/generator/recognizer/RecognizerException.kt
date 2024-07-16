package org.olafneumann.regex.generator.recognizer

import org.olafneumann.regex.generator.RegexGeneratorException

class RecognizerException(
    override val message: String,
    override val cause: Exception? = null
) : RegexGeneratorException(message)
