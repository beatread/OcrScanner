package com.app.ocrscanner.validator

import com.app.ocrscanner.Utils.Constants.Validation.MAX_USREOU_LENGTH
import com.app.ocrscanner.Utils.Constants.Validation.MIN_USREOU_LENGTH
import com.app.ocrscanner.validator.base.OnValidateOcrResult
import java.util.regex.Pattern

class UsreouValidator() : OnValidateOcrResult {
    override fun isValid(text: String?): Boolean {
        if (text != null && text.length in MIN_USREOU_LENGTH..MAX_USREOU_LENGTH) {
            val pattern = Pattern.compile(
                ("([0-9]{"
                        + MIN_USREOU_LENGTH
                        ) + "," + MAX_USREOU_LENGTH
                    .toString() + "})"
            )
            val matcher = pattern.matcher(text)
            return matcher.find()
        }
        return false
    }

    override fun formatResultByWord(word: String): String {
        return word.replace("[^0-9]".toRegex(), "")
    }
}