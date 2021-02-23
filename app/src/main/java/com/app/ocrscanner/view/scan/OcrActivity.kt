package com.app.ocrscanner.view.scan

import android.app.Activity
import android.content.Intent
import android.os.*
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.app.ocrscanner.R
import com.app.ocrscanner.Utils.Constants
import com.app.ocrscanner.Utils.Constants.Extras.OCR_RESULT
import com.app.ocrscanner.Utils.Constants.Extras.OCR_SCAN_MODE
import com.app.ocrscanner.Utils.Constants.Extras.OCR_VALIDATOR
import com.app.ocrscanner.Utils.Constants.Extras.TITLE
import com.app.ocrscanner.databinding.ActivityOcrBinding
import com.app.ocrscanner.text_recognition.TextRecognition
import com.app.ocrscanner.validator.base.OnValidateOcrResult
import com.otaliastudios.cameraview.controls.Audio
import kotlinx.coroutines.*

class OcrActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val ON_SUCCESS_RESULT_FINISH_DELAY = 2000L
        private const val CAMERA_AUTOFOCUS_DELAY = 3000

        fun performTransaction(
            context: Activity,
            ocrValidator: OnValidateOcrResult,
            title: String,
            requestCode: Int,
            scanMode: Constants.OcrScanMode
        ) {
            Intent(context, OcrActivity::class.java).apply {
                putExtra(TITLE, title)
                putExtra(OCR_VALIDATOR, ocrValidator)
                putExtra(OCR_SCAN_MODE, scanMode.name)
                context.startActivityForResult(this, requestCode)
            }
        }
    }

    private lateinit var binding: ActivityOcrBinding

    private var title: String? = null
    private lateinit var validator: OnValidateOcrResult
    private var recognizer: TextRecognition? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(R.layout.activity_ocr)
        binding = ActivityOcrBinding.bind(findViewById(R.id.root_view))
        binding.btnClose.setOnClickListener(this)

        intent.extras.let { extras ->
            if (extras != null) {
                title = extras.getString(TITLE)
                validator = extras.getSerializable(OCR_VALIDATOR) as OnValidateOcrResult
                Constants.OcrScanMode.valueOf(extras.getString(OCR_SCAN_MODE)!!)
            } else {
                validator = object : OnValidateOcrResult {
                    override fun isValid(text: String?): Boolean {
                        return false
                    }
                }
                Constants.OcrScanMode.BY_WORD
            }
        }.let { scanMode ->
            binding.apply {
                camera.apply {
                    setLifecycleOwner(this@OcrActivity)
                    autoFocusResetDelay = CAMERA_AUTOFOCUS_DELAY.toLong()
                    audio = Audio.OFF
                }
                ocrDetectingArea.apply {
                    text = title
                    post {
                        recognizer =
                            TextRecognition(
                                getRecognitionRect(),
                                scanMode,
                                object : OnTextRecognizedListener {
                                    override fun onTextRecognised(text: String) {
                                        if (scanMode == Constants.OcrScanMode.BY_LINE) {
                                            validator.formatResultByLine(text)
                                        } else {
                                            validator.formatResultByWord(text)
                                        }.let {
                                            if (validator.isValid(text)) {
                                                onTextRecognized(text)
                                            }
                                        }
                                    }

                                })
                        camera.addFrameProcessor(recognizer!!::processFrame)
                    }
                }
            }
        }
    }

    private fun onTextRecognized(result: String) {
        (getSystemService(VIBRATOR_SERVICE) as Vibrator).let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                it.vibrate(500)
            }
        }
        binding.ocrDetectingArea.setResult(result)
        recognizer!!.onStop()
        val intent = Intent()
        intent.putExtra(OCR_RESULT, result)
        setResult(RESULT_OK, intent)
        MainScope().launch(Dispatchers.IO) {
            delay(ON_SUCCESS_RESULT_FINISH_DELAY)
            finish()
        }
    }

    override fun onClick(view: View?) {
        if (view == binding.btnClose) {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}