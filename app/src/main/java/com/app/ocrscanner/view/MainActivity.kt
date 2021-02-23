package com.app.ocrscanner.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.ocrscanner.R
import com.app.ocrscanner.Utils.Constants
import com.app.ocrscanner.Utils.Constants.Extras.OCR_RESULT
import com.app.ocrscanner.databinding.ActivityMainBinding
import com.app.ocrscanner.validator.IbanOcrValidator
import com.app.ocrscanner.validator.UsreouValidator
import com.app.ocrscanner.view.scan.OcrActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {
    enum class RequestCode{
        USREOU, IBAN
    }
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.bind(findViewById(R.id.root_view))

        binding.apply {
            btnScanIban.setOnClickListener(this@MainActivity)
            btnScanUsreou.setOnClickListener(this@MainActivity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) return
        binding.apply {
            when(requestCode) {
                RequestCode.USREOU.ordinal -> etxtUsreou.setText(data?.getStringExtra(OCR_RESULT) ?: "")
                RequestCode.IBAN.ordinal -> etxtIban.setText(data?.getStringExtra(OCR_RESULT) ?: "")
            }
        }

    }

    override fun onClick(view: View?) {
       binding.apply {
           when(view) {
               btnScanUsreou -> {
                   OcrActivity.performTransaction(
                       this@MainActivity,
                       UsreouValidator(),
                       resources.getString(R.string.place_edrpou_within_frame),
                       RequestCode.USREOU.ordinal,
                       Constants.OcrScanMode.BY_WORD
                   )
               }
               btnScanIban -> {
                   OcrActivity.performTransaction(
                       this@MainActivity,
                       IbanOcrValidator(),
                       resources.getString(R.string.place_iban_within_frame),
                       RequestCode.IBAN.ordinal,
                       Constants.OcrScanMode.BY_LINE
                   )
               }
           }
       }
    }
}