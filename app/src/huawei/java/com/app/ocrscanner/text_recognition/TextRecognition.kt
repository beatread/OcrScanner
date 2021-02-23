package com.app.ocrscanner.text_recognition

import android.graphics.Bitmap
import android.graphics.RectF
import android.media.Image
import android.util.Log
import com.app.ocrscanner.Utils.Constants
import com.app.ocrscanner.Utils.getGrayedBitmapFromByteArray
import com.app.ocrscanner.Utils.rotate
import com.app.ocrscanner.view.scan.OnTextRecognizedListener
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.MLFrame
import com.huawei.hms.mlsdk.text.MLLocalTextSetting
import com.huawei.hms.mlsdk.text.MLTextAnalyzer
import com.otaliastudios.cameraview.frame.Frame
import java.io.IOException

class TextRecognition(
    private val detectingArea: RectF,
    private val scanMode: Constants.OcrScanMode,
    private val onTextRecognized: OnTextRecognizedListener
) {

    private var textAnalyzer: MLTextAnalyzer? = null

    private var nextFrame: MLFrame? = null
    private var isInProcess = false

    init {
        MLLocalTextSetting.Factory()
            .setOCRMode(MLLocalTextSetting.OCR_DETECT_MODE)
            .setLanguage("en")
            .create().let {
                textAnalyzer = MLAnalyzerFactory.getInstance().getLocalTextAnalyzer(it)
            }
    }

    fun processFrame(frame: Frame) {
        frame.apply {
            if (dataClass == ByteArray::class.java) {
                (getData() as ByteArray).getGrayedBitmapFromByteArray(size.width, size.height)
                    .rotate(rotationToUser.toFloat()).apply {
                        Bitmap.createBitmap(
                            this,
                            detectingArea.left.toInt(),
                            detectingArea.top.toInt(),
                            detectingArea.right.toInt(),
                            detectingArea.bottom.toInt() - detectingArea.top.toInt()
                        ).let {
                            nextFrame = MLFrame.fromBitmap(it)
                        }
                    }
            } else if (dataClass == Image::class.java) {
                nextFrame = MLFrame.fromMediaImage(getData(), rotationToUser)
            }
            if (nextFrame == null) {
                Log.v("Image is null", "frame class is neither byte[] or Image")
                return
            }
            if (!isInProcess) {
                isInProcess = true
                processNextFrame(nextFrame!!)
            }
        }
    }

    private fun processNextFrame(image: MLFrame?) {
        if (image == null) return
        textAnalyzer?.asyncAnalyseFrame(image)?.apply {
            addOnCompleteListener { processNextFrame(nextFrame) }
            addOnFailureListener { Log.v("TextRecognizer", "Failed") }
            addOnCanceledListener { Log.v("Recognizer: ", "Canceled") }
            addOnSuccessListener() { text ->
                text.blocks.let { blocks ->
                    if (blocks.size == 0) {
                        Log.v("TextRecognizer: ", "Success: no text")
                        return@addOnSuccessListener
                    }
                    for (block in blocks) {
                        for (line in block.contents) {
                            if (scanMode == Constants.OcrScanMode.BY_LINE) {
                                onTextRecognized.onTextRecognised(line.stringValue)
                            } else {
                                for (word in line.contents) {
                                    onTextRecognized.onTextRecognised(word.stringValue)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun onStop() {
        nextFrame = null
        textAnalyzer?.apply {
            try {
                stop()
                textAnalyzer = null
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}