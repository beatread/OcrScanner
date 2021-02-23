package com.app.ocrscanner.Utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.TypedValue


fun ByteArray.getGrayedBitmapFromByteArray(width: Int, height: Int): Bitmap {
    val pixCount = width * height
    val intGreyBuffer = IntArray(pixCount)
    for (i in 0 until pixCount) {
        val greyValue = this[i].toInt() and 0xff
        intGreyBuffer[i] = -0x1000000 or (greyValue shl 16) or (greyValue shl 8) or greyValue
    }
    return Bitmap.createBitmap(intGreyBuffer, width, height, Bitmap.Config.ARGB_8888)
}

fun Bitmap.rotate(angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}

fun Float.toDp(context: Context): Float{
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        context.resources.displayMetrics
    )
}

fun Float.toSp(context: Context): Float{
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        context.resources.displayMetrics
    )
}

fun String.removeSpaces(): String {
    return this.replace("\\s+".toRegex(), "")
}
