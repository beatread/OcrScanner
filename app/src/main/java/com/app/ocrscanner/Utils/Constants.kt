package com.app.ocrscanner.Utils

class Constants {

    enum class OcrScanMode {
        BY_WORD, BY_LINE
    }

    object Validation {
        const val MIN_USREOU_LENGTH = 8
        const val MAX_USREOU_LENGTH = 10
        const val MAX_IBAN_LENGHT = 29
        const val IBAN_COUNTRY_CODE = "UA"
    }

    object Extras {
        const val TITLE = "title"
        const val OCR_VALIDATOR = "ocr_validator"
        const val OCR_SCAN_MODE = "ocr_scan_mode"
        const val OCR_RESULT = "ocr_result"
    }
}