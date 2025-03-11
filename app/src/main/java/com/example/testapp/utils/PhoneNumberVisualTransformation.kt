package com.example.testapp.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Маска для номера телефона: +7(XXX)-XXX-XX-XX
        val trimmed = if (text.text.length >= 10) text.text.substring(0, 10) else text.text
        var out = "+7("
        for (i in trimmed.indices) {
            out += trimmed[i]
            when (i) {
                2 -> out += ")-"
                5 -> out += "-"
                7 -> out += "-"
            }
        }
        return TransformedText(AnnotatedString(out), PhoneNumberOffsetMapper)
    }

    private val PhoneNumberOffsetMapper = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            return when (offset) {
                0 -> 3
                1 -> 4
                2 -> 5
                3 -> 8
                4 -> 9
                5 -> 10
                6 -> 12
                7 -> 13
                8 -> 15
                9 -> 16
                else -> 17
            }
        }

        override fun transformedToOriginal(offset: Int): Int {
            return when {
                offset <= 3 -> 0
                offset <= 5 -> 1
                offset <= 7 -> 2
                offset <= 8 -> 3
                offset <= 10 -> 4
                offset <= 11 -> 5
                offset <= 13 -> 6
                offset <= 14 -> 7
                offset <= 16 -> 8
                else -> 10
            }
        }
    }
}