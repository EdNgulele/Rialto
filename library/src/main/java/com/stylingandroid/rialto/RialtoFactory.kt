package com.stylingandroid.rialto

import android.text.Annotation
import android.text.SpannableStringBuilder
import android.text.SpannedString

class RialtoFactory(private val registry: Registry = Registry()) {

    fun registerSpanFactory(key: String, value: String, creator: () -> Any) {
        registry.registerFactory(key, value, creator)
    }

    fun processAnnotations(text: CharSequence?): CharSequence? {
        return if (text is SpannedString) {
            processAnnotations(text, SpannableStringBuilder(text))
        } else {
            text
        }
    }

    internal fun processAnnotations(
        spannedString: SpannedString,
        spannableStringBuilder: SpannableStringBuilder
    ): CharSequence {
        spannedString.getSpans(0, spannedString.length, Annotation::class.java)
            .forEach { annotation ->
                annotation.applySpan(
                    spannedString.getSpanStart(annotation),
                    spannedString.getSpanEnd(annotation),
                    spannableStringBuilder
                )
            }
        return spannableStringBuilder
    }

    private fun Annotation.applySpan(start: Int, end: Int, spannableStringBuilder: SpannableStringBuilder) =
        registry[key, value].forEach { factory ->
            spannableStringBuilder.setSpan(factory.invoke(), start, end, SpannedString.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
}

