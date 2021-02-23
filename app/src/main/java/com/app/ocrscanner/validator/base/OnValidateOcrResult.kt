package com.app.ocrscanner.validator.base

import java.io.Serializable

interface OnValidateOcrResult : Serializable {
    fun isValid(text: String?): Boolean

    fun formatResultByLine(line: String): String? {
        return line
    }

    fun formatResultByWord(word: String): String? {
        return word
    }
}