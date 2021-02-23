package com.app.ocrscanner.text_recognition

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.RectF
import android.media.Image
import android.util.Log
import com.app.ocrscanner.Utils.Constants
import com.app.ocrscanner.Utils.getGrayedBitmapFromByteArray
import com.app.ocrscanner.Utils.rotate
import com.app.ocrscanner.view.scan.OnTextRecognizedListener
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.otaliastudios.cameraview.frame.Frame

class TextRecognition(
    private val detectingArea: RectF,
    private val scanMode: Constants.OcrScanMode,
    private val onTextRecognized: OnTextRecognizedListener
) {

    private val recognizer: TextRecognizer = TextRecognition.getClient()

    private var nextFrame: InputImage? = null
    private var isInProcess = false

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
                            nextFrame = InputImage.fromBitmap(it, 0)
                        }
                    }
            } else if (dataClass == Image::class.java) {
                nextFrame = InputImage.fromMediaImage(getData(), rotationToUser)
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

    private fun processNextFrame(image: InputImage?) {
        if (image == null) return
        recognizer.process(image)
            .addOnCompleteListener { processNextFrame(nextFrame) }
            .addOnFailureListener { Log.v("TextRecognizer", "Failed") }
            .addOnCanceledListener { Log.v("Recognizer: ", "Canceled") }
            .addOnSuccessListener() { text ->
                text.textBlocks.let { blocks ->
                    if (blocks.size == 0) {
                        Log.v("TextRecognizer: ", "Success: no text")
                        return@addOnSuccessListener
                    }
                    for (block in blocks) {
                        for (line in block.lines) {
                            if (scanMode == Constants.OcrScanMode.BY_LINE) {
                                onTextRecognized.onTextRecognised(line.text)
                            } else {
                                for (element in line.elements) {
                                    onTextRecognized.onTextRecognised(element.text)
                                }
                            }
                        }
                    }
                }
            }
    }

    fun onStop() {
        nextFrame = null
        recognizer.close()
    }
}