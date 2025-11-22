package demo.ocr.camera.interfaces

import android.graphics.Bitmap
import demo.ocr.camera.utils.ImageMetaData
import demo.ocr.camera.utils.ImageProcessingGate

fun interface ImageFrameListener {
    fun onImageAvailable(bitmap: Bitmap?, imageMetadata: ImageMetaData?, imageProcessingGate: ImageProcessingGate)
 }