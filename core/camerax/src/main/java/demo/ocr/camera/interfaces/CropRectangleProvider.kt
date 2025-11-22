package demo.ocr.camera.interfaces

import android.graphics.RectF

interface CropRectangleProvider {
    fun getCropRect(rotateValue: Boolean = true): RectF
    fun getViewRect(): RectF
    fun setSize(width: Int, height: Int)
    fun setRoiChangedCallback(roiChangeListener: RoiChangeListener)
}

fun interface RoiChangeListener {
    fun onEventOccurred()
}