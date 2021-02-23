package com.app.ocrscanner.validator

import com.app.ocrscanner.Utils.Constants
import com.app.ocrscanner.Utils.Constants.Validation.IBAN_COUNTRY_CODE
import com.app.ocrscanner.Utils.Constants.Validation.MAX_IBAN_LENGHT
import com.app.ocrscanner.Utils.iban.IbanValidator
import com.app.ocrscanner.Utils.removeSpaces
import com.app.ocrscanner.validator.base.OnValidateOcrResult
import java.util.regex.Pattern

class IbanOcrValidator : OnValidateOcrResult {

    override fun isValid(text: String?): Boolean {
        return if (text == null || text.isBlank()) false else IbanValidator.getInstance()
            .isValid(text)
    }

    override fun formatResultByLine(line: String): String? {
        line.removeSpaces().let{
            if (it.isBlank()) return null
            val digitsLength: Int = MAX_IBAN_LENGHT - IBAN_COUNTRY_CODE.length
            val pattern = Pattern.compile("[Uu][Aa]\\d{$digitsLength}")
            val matcher = pattern.matcher(it)
            return if (matcher.find()) {
                it.substring(matcher.start(), matcher.end())
            } else {
                null
            }
        }
    }
}
