package demo.ocr.camera.interfaces

import android.graphics.Bitmap
import demo.ocr.camera.utils.ImageMetaData

fun interface ImageCaptureListener {
    fun onImageAvailable(bitmap: Bitmap?, imageMetadata: ImageMetaData?)
 }