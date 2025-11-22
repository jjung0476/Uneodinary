package demo.ocr.camera.interfaces

import androidx.lifecycle.LifecycleOwner

interface CameraRepository {
    fun startCamera(lifecycleOwner: LifecycleOwner, isAutoMode: Boolean)
    fun stopCamera()

    fun registerImageListener(imageFrameListener: ImageFrameListener)
    fun captureImage(imageCaptureListener: ImageCaptureListener)
    fun setResolution(width: Int, height: Int)
    fun pause()
    fun resume()
}